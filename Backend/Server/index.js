const cors = require("cors");
const express = require("express");
const fs = require("fs");
const mysql = require("mysql2");

const app = express();
const PORT = 3000;

app.use(express.json());
app.use(
  cors({
    origin: function (origin, callback) {
      return callback(null, true);
    },
  })
);

//Creacío connextió al LABS
var con = mysql.createConnection({
  host: "labs.inspedralbes.cat",
  user: "a19teomerrod_user",
  password: "Pedralbes22_23",
  database: "a19teomerrod_project",
});

//Executar connexió
con.connect(function (err) {
  if (err) throw err;
  else {
    console.log("Connexio establerta");
  }
});

//Agafar els users
app.post("/getUsers", (req, res) => {
  con.query("SELECT * FROM PERSONA", function (err, result, fields) {
    if (err) throw err;
    res.json(result);
  });
});

//Agafar els admins
app.post("/getAdmins", (req, res) => {
  var dades = [];
  con.query("SELECT * FROM PERSONA", function (err, result, fields) {
    dades = result;
    res.json(dades);
    console.log(dades);
  });
});

//Agafar els productes
app.post("/getProducts", (req, res) =>{
    con.query("SELECT * FROM PRODUCTE", function(err, result, fields){
        if (err) throw err;
        res.json(result);
    });
});

//Veure tots els usuaris al Servidor
app.post("/seeUsers", (req, res) => {
  var dades = [];
  console.log(req.body.values[0]);
  con.query(
    "SELECT * FROM PERSONA WHERE PERSONA.email = '" + req.body.values[0] + "'",
    function (err, result, fields) {
      res.json(result);
    }
  );
});

//Registre USER nou APP
app.post("/registerNewUser", (req, res) => {
  let auth = true;
  con.query(
    "SELECT email, user FROM PERSONA WHERE email = '" + req.body.email + "' or user = '" + req.body.user + "'",
    function (err, result, fields) {
      if (result != 0) {
        auth = false;
      } else {
        con.query("INSERT INTO PERSONA VALUES ('" + req.body.email + "','" + req.body.nom + "','" + req.body.cognoms + "','" + req.body.edad + "','" + req.body.ubicacio + "','" + req.body.user + "','" + req.body.pass + "','" + req.body.descripcio + "','" + req.body.rol + "')");
      }
    }
  );
  res.json(auth);
});

//Validació LOGIN a l'APP
app.get("/validarLogIn/:txtUserSignIn/:txtPasswordSignIn", (req, res) => {
  let user = req.params.txtUserSignIn;
  let passwd = req.params.txtPasswordSignIn;
  console.log("USER: " + user);
  console.log("PASS: " + passwd);
  let auth = false;
  con.query(
    "SELECT user, pass FROM PERSONA WHERE user = '" +
      user +
      "' && pass ='" +
      passwd +
      "'",
    function (err, result, fields) {
      console.log(JSON.stringify(result));
      if (result != 0) {
        auth = true;
        res.send(auth);
      } else {
        res.send(auth);
      }
    }
  );
});

app.post("/ferAdmin", (req, res) => {
    if (req.body.values[1] == "admin")
    {
        con.query("UPDATE PERSONA SET PERSONA.rol = 'user' WHERE PERSONA.email = '" + req.body.values[0] + "'", function(err, result, field){
            res.json(result);
        });
    }else{
        con.query("UPDATE PERSONA SET PERSONA.rol = 'admin' WHERE PERSONA.email = '" + req.body.values[0] + "'", function(err, result, field){
            res.json(result);
        });
    }
});




//Filtra per usuari els productes que es demanen
app.post("/getProductUser", (req, res) => {
  var data = [];
  var emailUser = "x@i";
  con.query(
    "SELECT nom FROM PRODUCTE WHERE correu_usu='" + emailUser + "';",
    function (err, result, fields) {
      if (err) throw err;
      data.push(result);
      res.json(data);
    }
  );
});

/*Obrir Servidor*/
app.listen(PORT, () => {
  console.log("Server RUNNING [" + PORT + "]");
});
