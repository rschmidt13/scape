#!/bin/bash
sudo dtruss -f /Applications/Preview.app/Contents/MacOS/Preview $1 > dtruss-preview.log 2>&1