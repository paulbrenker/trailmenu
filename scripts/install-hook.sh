#!/bin/sh

hook_file=$(dirname "$")/.git/hooks/pre-commit

cat >"$hook_file" <<EOF
#!/bin/sh

# No commiting to main

if [ "\$(git rev-parse --abbrev-ref HEAD)" = "main"]; then
  echo "You cannot commit directly to main branch"
  exit 1
fi

# Check that kotlin files are formatted before commiting

STAGED_FILES=\$(git diff --staged --name-only --diff-filter=ACMRTUXB | grep ".kt")

if [ -z "\$STAGED_FILES" ]; then
  echo "No changed Kotlin files"
  exit 0
fi

echo "Run Ktlint check"
./gradlew --quiet ktlintCheck -PinternalKtlintGitFilter=\$STAGED_FILES
EXIT_CODE=\$?

if [ \$EXIT_CODE -ne 0 ]; then
  echo "*********************"
  echo "* Ktlint check fail *"
  echo "*                   *"
  echo "* Fix and try again *"
  echo "*********************"
  exit \$EXIT_CODE
fi
EOF

chmod +x "$hook_file"