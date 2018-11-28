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
	{
		"hostname": "api.bintray.com",
		"path": `/packages/${process.env.BINTRAY_SUBJECT}/${process.env.BINTRAY_REPO}/${process.env.BINTRAY_PACKAGE}/versions`,
		"method": "POST",
		"auth": `${process.env.BINTRAY_USER}:${process.env.BINTRAY_APITOKEN}`,
	},
	(res) =>
	{
		if (res.statusCode < 200 || res.statusCode > 399)
			throw "Status code " + res.statusCode;
		
		var data = "";
		res.on('data', (chunk) =>
		{
			data += chunk;
		});
		
		res.on('end', () =>
		{
			console.log(`${res.statusCode}=${data}`)
		});
	});

// Write request
req.write(JSON.stringify(
	{
		"name": `${process.argv[0]}`,
		"desc": `${process.argv[0]}`,
	}));
req.end();

