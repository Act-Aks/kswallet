from pathlib import Path

ROOT = Path.cwd()
OUTPUT = ROOT / "repo-review-export"
MAX_BUNDLE_BYTES = 1_500_000

SKIP_DIRS = {
    ".git", ".gradle", ".idea", ".kotlin",
    "build", "out", "captures", "node_modules",
    "repo-review-export",
}

SKIP_FILES = {
    "local.properties",
    "google-services.json",
    "google-services.xml",
    "keystore.properties",
    "key.properties",
    "secrets.properties",
}

SOURCE_SUFFIXES = {
    ".kt", ".kts", ".java", ".xml",
    ".toml", ".md", ".json",
    ".yaml", ".yml", ".sql", ".sq",
    ".pro", ".properties",
}

SPECIAL_NAMES = {
    "LICENSE", "LICENSE.txt", "NOTICE",
    ".gitignore", ".editorconfig",
}

OUTPUT.mkdir(exist_ok=True)

for old_file in OUTPUT.glob("ivy-*.txt"):
    old_file.unlink()

files = []
tree = []
skipped = []

for path in sorted(ROOT.rglob("*")):
    relative = path.relative_to(ROOT)

    if any(part in SKIP_DIRS for part in relative.parts):
        continue

    if not path.is_file():
        continue

    tree.append(relative.as_posix())

    if path.name in SKIP_FILES or path.name.startswith(".env"):
        continue

    if (
        path.suffix.lower() not in SOURCE_SUFFIXES
        and path.name not in SPECIAL_NAMES
    ):
        continue

    try:
        content = path.read_text(encoding="utf-8")
    except (UnicodeError, OSError):
        skipped.append(relative.as_posix())
        continue

    files.append((relative.as_posix(), content))

(OUTPUT / "ivy-00-tree.txt").write_text(
    "\n".join(tree),
    encoding="utf-8",
)

bundle = []
bundle_bytes = 0
bundle_index = 1

def write_bundle():
    global bundle, bundle_bytes, bundle_index

    if not bundle:
        return

    destination = OUTPUT / f"ivy-{bundle_index:02d}-source.txt"
    destination.write_text("".join(bundle), encoding="utf-8")
    bundle = []
    bundle_bytes = 0
    bundle_index += 1

for relative, content in files:
    section = (
        f"\n{'=' * 80}\n"
        f"FILE: {relative}\n"
        f"{'=' * 80}\n"
        f"{content}\n"
    )

    section_bytes = len(section.encode("utf-8"))

    if bundle and bundle_bytes + section_bytes > MAX_BUNDLE_BYTES:
        write_bundle()

    bundle.append(section)
    bundle_bytes += section_bytes

write_bundle()

if skipped:
    (OUTPUT / "ivy-skipped-files.txt").write_text(
        "\n".join(skipped),
        encoding="utf-8",
    )

print(f"Exported {len(files)} source/configuration files.")
print(f"Created {bundle_index - 1} source bundles.")
print(f"Output directory: {OUTPUT}")
