// get jwt token for the username

var express = require("express");
var uuid = require("uuid").v4;
const jwt = require("jsonwebtoken");
const fs = require("fs");
const path = require("path");

var router = express.Router();

const privateKey = fs.readFileSync(path.resolve(__dirname, "pk.txt"), "utf8");

const secret = "vpaas-magic-cookie-a4a17f5348dc4ac099eb24c42a83bc7a/42d5f5";

router.get("/", function (req, res, next) {
  const username = req.query.username;
  const password = req.query.password;
  if (!username) {
    return res.status(400).json({ error: "Username is required" });
  }

  const payload = {
    aud: "jitsi",
    context: {
      user: {
        id: uuid(),
        name: username,
        email: `${username}@test.com`,
        moderator: password === "1234" ? true : false,
      },
    },
    room: "*",
    iss: "chat", // This should be your Jitsi App ID or similar
    sub: "vpaas-magic-cookie-a4a17f5348dc4ac099eb24c42a83bc7a", // This should be your Jitsi server domain
    exp: Math.floor(Date.now() / 1000) + 1 * 60 * 60, // Token expires in 1 hour
    nbf: Math.floor(Date.now() / 1000) - 60 * 5,
  };

  const token = jwt.sign(payload, privateKey, {
    algorithm: "RS256",
    header: {
      kid: secret, // Key ID, use as needed
      typ: "JWT",
    },
  });
  console.log(token);
  console.log(payload);

  res.json({ token });
});

module.exports = router;
