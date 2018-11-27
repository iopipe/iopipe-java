#!/usr/bin/env node

const util = require("util");
const https = require("https");

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

// Make request
const req = https.request(
	`https://bintray.com/api/v1/packages/${process.env.BINTRAY_SUBJECT}/${process.env.BINTRAY_REPO}/${process.env.BINTRAY_PACKAGE}/versions`,
	{
		"method": "POST",
	},
	(res) =>
	{
		console.log(res);
	});

// We just want to stop here
req.on('error', (e) => {
		console.error("oops " + e);
		throw e;
	});

// Write request
req.write(JSON.stringify(
	{
		"name": `${process.argv[0]}`,
		"desc": `${process.argv[0]}`,
	}));
req.end();

