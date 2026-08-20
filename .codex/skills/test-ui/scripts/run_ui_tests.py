"""Run UI test cases documented in test/ui-test-plan.md."""

from __future__ import annotations

import argparse
import os
import re
import subprocess
import sys
import tempfile
from pathlib import Path


CASE = re.compile(
    r"^## (?P<name>[^\r\n]+)\r?\n\r?\n\*\*Aim:\*\* (?P<aim>.+?)\r?\n\r?\n"
    r"\*\*Input:\*\*\r?\n```text\r?\n(?P<input>.*?)\r?\n```\r?\n\r?\n"
    r"\*\*Expected output:\*\*\r?\n```text\r?\n(?P<expected>.*?)\r?\n```",
    re.DOTALL | re.MULTILINE,
)


def normalise(text: str) -> str:
    """Normalise line endings for cross-platform output comparisons."""
    return text.replace("\r\n", "\n").replace("\r", "\n")


def parse_cases(plan_path: Path) -> list[dict[str, str]]:
    """Return cases from the required Markdown plan format."""
    cases = [match.groupdict() for match in CASE.finditer(plan_path.read_text(encoding="utf-8"))]
    if not cases:
        raise ValueError("No valid UI cases found in test/ui-test-plan.md.")
    return cases


def find_java_home(requested_home: str | None) -> Path:
    """Find Java 25, prioritising an explicit path over environment settings."""
    candidates = [Path(requested_home)] if requested_home else []
    if os.environ.get("JAVA_HOME"):
        candidates.append(Path(os.environ["JAVA_HOME"]))
    candidates.extend(sorted((Path.home() / ".jdks").glob("*25*"), reverse=True))
    for home in candidates:
        if (home / "bin" / "javac.exe").exists() or (home / "bin" / "javac").exists():
            return home
    raise FileNotFoundError("Java 25 was not found. Pass --java-home <JDK 25 directory>.")


def tool(java_home: Path, name: str) -> str:
    """Return the platform-specific Java tool path."""
    suffix = ".exe" if os.name == "nt" else ""
    path = java_home / "bin" / f"{name}{suffix}"
    if not path.exists():
        raise FileNotFoundError(f"Could not find {path}")
    return str(path)


def record(case: dict[str, str], actual: str, passed: bool) -> str:
    """Format one complete, human-readable console session record."""
    status = "PASS" if passed else "FAIL"
    return (
        f"## {status}: {case['name']}\n\nAim: {case['aim']}\n\n"
        f"### Console input\n\n```text\n{case['input']}\n```\n\n"
        f"### Expected output\n\n```text\n{case['expected']}\n```\n\n"
        f"### Actual output\n\n```text\n{actual}```\n\n"
    )


def write_session(path: Path, records: list[str]) -> None:
    """Save all completed cases, including the first failure if present."""
    path.write_text("# UI Test Session\n\n" + "".join(records), encoding="utf-8")


def main() -> int:
    """Compile Pathfinder and run test cases until the first mismatch."""
    parser = argparse.ArgumentParser()
    parser.add_argument("--java-home", help="Path to the JDK 25 home directory")
    parser.add_argument("--timeout", type=float, default=10, help="Seconds allowed per test")
    arguments = parser.parse_args()
    root = Path.cwd()
    session_path = root / "test" / "ui-test-session.md"
    cases = parse_cases(root / "test" / "ui-test-plan.md")
    java_home = find_java_home(arguments.java_home)
    sources = sorted((root / "src" / "main" / "java").glob("*.java"))

    with tempfile.TemporaryDirectory(prefix="pathfinder-ui-tests-") as classes:
        compilation = subprocess.run(
            [tool(java_home, "javac"), "-d", classes, *map(str, sources)],
            text=True, capture_output=True, check=False,
        )
        if compilation.returncode:
            failure = "## FAIL: Compilation\n\n```text\n" + normalise(
                compilation.stdout + compilation.stderr
            ) + "```\n"
            write_session(session_path, [failure])
            print(failure)
            return 1

        records: list[str] = []
        for case in cases:
            try:
                result = subprocess.run(
                    [tool(java_home, "java"), "-cp", classes, "Pathfinder"],
                    input=case["input"] + "\n", text=True, capture_output=True,
                    timeout=arguments.timeout, check=False,
                )
                actual = normalise(result.stdout + result.stderr)
            except subprocess.TimeoutExpired as error:
                actual = normalise((error.stdout or "") + "\n[TIMED OUT]\n")

            passed = actual == normalise(case["expected"] + "\n")
            transcript = record(case, actual, passed)
            records.append(transcript)
            write_session(session_path, records)
            print(transcript)
            if not passed:
                print("Test session stopped after the first failure.")
                print(f"Transcript: {session_path}")
                return 1

    print(f"All {len(cases)} test case(s) passed.")
    print(f"Transcript: {session_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
