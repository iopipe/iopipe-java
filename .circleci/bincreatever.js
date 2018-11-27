#!/usr/bin/env node

// The API is documented here:
// https://www.jfrog.com/confluence/display/BT/Bintray+REST+API

// *** Create new version
// HTTP -> POST /packages/:subject/:repo/:package/versions
// BODY -> {
//   "name": "1.1.5",
//   "released": "ISO8601 (yyyy-MM-dd'T'HH:mm:ss.SSSZ)", (optional)
//   "desc": "This version...",
//   "github_release_notes_file": "RELEASE.txt", (optional)
//   "github_use_tag_release_notes": true, (optional)
//   "vcs_tag": "1.1.5" (optional)
// }
// RESULT -> Status: 201 Created
// {Version get JSON response}
throw "TODO";

