const cors = require("cors");
const express = require("express");
const fs = require("fs");
const path = require("path");
const mysql = require("mysql2");
const multer = require('multer');
const bodyParser = require('body-parser');
const e = require("cors");

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
//Llegir els valors del fitxer JSON
var rutaFitxer = path.join(__dirname + '/server_settings.json');
var dadesServer = JSON.parse(fs.readFileSync(rutaFitxer, 'utf-8'));

var con = mysql.createConnection({
  host: dadesServer.host,
  user: dadesServer.user,
  password: dadesServer.password,
  database: dadesServer.database,
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
    //console.log(dades);
  });
});

//Agafar els productes
app.post("/getProducts", (req, res) => {
  con.query("SELECT * FROM PRODUCTE JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) ", function (err, result, fields) {
    if (err) throw err;
    //console.log(result);
    res.json(result);
  });
});

app.post("/seeProduct", (req, res) =>{
  console.log(req.body.values[0]);
  con.query("SELECT * FROM PRODUCTE JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) WHERE PRODUCTE.id_producte = '" + req.body.values[0] + "'", function (err, result, fields) {
    if (err) throw err;
    //console.log(result);
    res.json(result);
  });
});

//Veure tots els usuaris al Servidor
app.post("/seeUsers", (req, res) => {
  var dades = [];
  //console.log(req.body.values[0]);
  con.query(
    "SELECT * FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload) WHERE PERSONA.email = '" + req.body.values[0] + "'",
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
        con.query("INSERT INTO PERSONA VALUES ('" + req.body.email + "','" + req.body.nom + "','" + req.body.cognoms + "','" + req.body.edad + "','" + req.body.ubicacio + "','" + req.body.user + "','" + req.body.pass + "','" + req.body.descripcio + "','" + req.body.rol + "',NULL ,NULL)");
      }
    }
  );
  res.json(auth);
});

//Validació LOGIN a l'APP
app.get("/validarLogIn/:txtUserSignIn/:txtPasswordSignIn", (req, res) => {
  let user = req.params.txtUserSignIn;
  let passwd = req.params.txtPasswordSignIn;
  //console.log("USER: " + user);
  //console.log("PASS: " + passwd);
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
  if (req.body.values[1] == "admin") {
    con.query("UPDATE PERSONA SET PERSONA.rol = 'user' WHERE PERSONA.email = '" + req.body.values[0] + "'", function (err, result, field) {
      res.json(result);
    });
  } else {
    con.query("UPDATE PERSONA SET PERSONA.rol = 'admin' WHERE PERSONA.email = '" + req.body.values[0] + "'", function (err, result, field) {
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


/* ---------------------- ENVIAR FITXERS A LA CARPETA ---------------------- */

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/user_images')
  },
  filename: function (req, file, cb) {
    con.query("SELECT email FROM `PERSONA` WHERE id_persona = (SELECT MAX(id_persona) FROM PERSONA);", function (err, result, field) {
      console.log("HEY RESULT -> " + JSON.stringify(result));
      var emailNomFile = JSON.stringify(result);
      emailNomFile = emailNomFile.replace(/\[/g, "");
      emailNomFile = emailNomFile.replace(/\{/g, "");
      emailNomFile = emailNomFile.replace(/\"/g, "");
      emailNomFile = emailNomFile.replace(/\:/g, "_");
      emailNomFile = emailNomFile.replace(/\}/g, "_");
      emailNomFile = emailNomFile.replace(/\]/g, "_");
      console.log("FINAL TEXT: " + emailNomFile);
      cb(null, file.fieldname + '-' + emailNomFile + '.jpg')
    });
  }
})

var upload = multer({ storage: storage })

app.post("/uploadUserImage", upload.single('myFile'), (req, res, next) => {
  const file = req.file
  if (!file) {
    const error = new Error('Please upload a file')
    error.httpStatusCode = 400
    console.log("error", 'Please upload a file');

    res.send({ code: 500, msg: 'Please upload a file' })
    return next({ code: 500, msg: error })
  }

  con.query("INSERT INTO UPLOADS VALUES (null, '" + JSON.stringify(req.file.path) + "')", function (err, result, field) {
    con.query("UPDATE PERSONA SET id_image = (SELECT max(id_upload) FROM UPLOADS) where id_persona = (SELECT MAX(id_persona) FROM PERSONA)", function (err, result, field) {
      res.send({ code: 200, msg: file });
    });
  });
})

app.post("/uploadProductImage", (req, res) => {
  //TO-DO Pujar un fitxer a la carpeta "uploads/product_images"
})

/*Obrir Servidor*/
app.listen(PORT, () => {
  console.log("Server RUNNING [" + PORT + "]");
});
