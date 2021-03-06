var express = require('express');
var router = express.Router();
var passport = require('passport');
var bodyParser = require("body-parser");

// Function gets users GID and returns what group in a string they belong to
whatGroup = (gid) => {
  if(gid == '500') return 'System Admin';
  if(gid == '300') return 'Committee';
  if(gid == '200') return 'Video Game Admin';
  else return 'member';
}

displayAdminButton = (gid) => {
  if (gid == '500') return 'block';
  else return 'none';
}

exports.root = (req, res, next) => {
    if (!req.isAuthenticated()) res.redirect('/auth/login');
    else res.render('index', {
      title: 'Index',
      user: req.user,
      groupName: whatGroup(req.user.gid),
      displayAdminButton: displayAdminButton(req.user.gid)
    });
}

exports.lost = (req, res, next) => {
    res.render('lost', {title: "Kang - Are you lost?"});
}
