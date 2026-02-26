#!/usr/bin/env bash
set -euo pipefail

# Simple VPS deployment script for BMD RegKassenTestTool Web
# Tested on Ubuntu 22.04+ / Debian 12+
#
# Usage:  ssh your-server 'bash -s' < deploy-vps.sh
# Or:     scp deploy-vps.sh your-server: && ssh your-server bash deploy-vps.sh

echo "=== BMD RegKassenTestTool — VPS Setup ==="

# 1. System dependencies
echo "→ Installing Node.js 22 + Java runtime..."
if ! command -v node &>/dev/null; then
  curl -fsSL https://deb.nodesource.com/setup_22.x | sudo -E bash -
  sudo apt-get install -y nodejs
fi

if ! command -v java &>/dev/null; then
  sudo apt-get update
  sudo apt-get install -y default-jre-headless
fi

echo "  Node: $(node --version)"
echo "  Java: $(java -version 2>&1 | head -1)"

# 2. Clone / update repo
REPO_DIR="$HOME/bmd-regkassen-web"
REPO_URL="https://github.com/MrGatzi/BMD_RegKassenTestTool.git"
BRANCH="cursor/next-js-verification-tool-fe14"

if [ -d "$REPO_DIR" ]; then
  echo "→ Updating existing repo..."
  cd "$REPO_DIR"
  git pull origin "$BRANCH"
else
  echo "→ Cloning repo..."
  git clone -b "$BRANCH" "$REPO_URL" "$REPO_DIR"
  cd "$REPO_DIR"
fi

# 3. Install web dependencies + build
echo "→ Installing dependencies and building..."
cd web
npm ci
npm run build

# 4. Start with pm2 (or just npm start)
echo "→ Starting server..."
if command -v pm2 &>/dev/null; then
  pm2 delete bmd-web 2>/dev/null || true
  pm2 start npm --name bmd-web -- start
  pm2 save
  echo "  Running via pm2. Use 'pm2 logs bmd-web' to view logs."
else
  echo "  TIP: Install pm2 for process management: npm i -g pm2"
  echo "  Starting with 'npm start' (Ctrl+C to stop)..."
  npm start
fi

echo ""
echo "=== Done! App running at http://$(hostname -I | awk '{print $1}'):3000 ==="
