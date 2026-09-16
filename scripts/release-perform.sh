#!/usr/bin/env bash
set -euo pipefail

repo_root="$(git rev-parse --show-toplevel)"
cd "$repo_root"

if [[ -n "$(git status --porcelain)" ]]; then
  echo "发布前检查失败：工作区存在未提交或未跟踪文件。" >&2
  git status --short >&2
  exit 1
fi

branch="$(git symbolic-ref --quiet --short HEAD || true)"
if [[ -z "$branch" ]]; then
  echo "发布前检查失败：当前处于 detached HEAD，无法检查未推送提交。" >&2
  exit 1
fi

upstream="$(git rev-parse --abbrev-ref "${branch}@{upstream}" 2>/dev/null || true)"
if [[ -z "$upstream" ]]; then
  echo "发布前检查失败：分支 '$branch' 没有配置 upstream，无法确认是否已推送。" >&2
  exit 1
fi

read -r behind ahead <<< "$(git rev-list --left-right --count "$upstream...HEAD")"
if [[ "$ahead" -ne 0 ]]; then
  echo "发布前检查失败：当前分支相对 '$upstream' 存在 $ahead 个未推送提交。" >&2
  exit 1
fi

exec mvn -Pprod release:perform "$@"
