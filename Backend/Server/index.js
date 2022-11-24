const cors = require("cors");
const express = require("express");
const fs = require("fs");
const path = require("path");
const mysql = require("mysql2");
const multer = require('multer');
const bodyParser = require('body-parser');
const e = require("cors");
const helmet = require('helmet');

const app = express();
const PORT = 7001;

const IP = "51.68.226.126"

app.use(express.json());
app.use(helmet.frameguard({ action: 'SAMEORIGIN' }));
app.use(
  cors({
    origin: function (origin, callback) {
      return callback(null, true);
    },
  })
);

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
  con.query("SELECT * FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload)", function (err, result, fields) {
    dades = result;
    res.json(dades);
  });
});

//Agafar els productes
app.post("/getProducts", (req, res) => {
    con.query("SELECT * FROM PRODUCTE JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) ORDER BY PRODUCTE.id_producte ASC", function (err, result, fields) {
      if (err) throw err;
      res.json(result);
    });
});

//Veure productes
app.post("/seeProduct", (req, res) =>{
  con.query("SELECT *, COUNT(MEGUSTA.id_producte) as likes FROM PRODUCTE JOIN MEGUSTA USING (id_producte) JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) WHERE PRODUCTE.id_producte = '" + req.body.values[0] + "'", function (err, result, fields) {
    if (err) throw err;
    res.json(result);
  }); 

});

//Veure tots els usuaris al Servidor
app.post("/seeUsers", (req, res) => {
  con.query(
    "SELECT * FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload) WHERE PERSONA.email = '" + req.body.values[0] + "'",
    function (err, result, fields) {
      res.json(result);
    }
  );
});

//Veure les peticions
app.post("/seePeticiones", (req, res) => {
  var dades = [];
  con.query(
    "SELECT PERSONA.user, PETICIONES.peticion, PETICIONES.id_usu FROM PETICIONES JOIN PERSONA ON (PETICIONES.id_usu = PERSONA.id)",
    function (err, result, fields) {
      res.json(result);
    }
  );
});

//Fer admin o no
app.post("/decidirPeticion", (req, res) => {
  var decision = req.body.values[1];
  var user = req.body.values[0];
  var id = req.body.values[2];
  
  if (decision == 1)
  {
    con.query(
      "DELETE FROM PETICIONES WHERE id_usu = " + id + "",
      function (err, result, fields) {
        res.json(result);
      }
    );
  }
  if (decision == 0)
  {
    con.query(
      "DELETE FROM PETICIONES WHERE id_usu = " + id + "",
      function (err, result, fields) {
        con.query(
          "UPDATE PERSONA SET PERSONA.rol = 'artista' WHERE PERSONA.id = '" + id + "'",
          function (err, result, fields) {
             res.json(result)
          }
        );
      }
    );

  }
});

app.post("/seeEdit", (req, res) => {
  var dades = [];
  con.query(
    "SELECT * FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload) WHERE PERSONA.id = '" + req.body.values[0] + "'",
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
        con.query("INSERT INTO PERSONA VALUES ('" + req.body.email + "','" + req.body.nom + "','" + req.body.cognoms + "','" + req.body.edad + "','" + req.body.ubicacio + "','" + req.body.user + "','" + req.body.pass + "','" + req.body.descripcio + "','" + req.body.rol + "',NULL ,1, 0)");
      }
    });
  res.json(auth);
});


//Editar usuari
app.post("/editUser/:id", (req, res) =>{
  var auth = true;
  con.query("SELECT email, user FROM PERSONA WHERE id != '" + req.body.id + "' and email = '" + req.body.email + "' or id != '" + req.body.id + "' and user = '" + req.body.user + "'", function(error, result, fields){ //And diff ID
    if (result != 0) {
      auth = false
    } else {
      con.query("UPDATE PERSONA SET email = '" + req.body.email + "', nom = '" +  req.body.nom + "', cognoms = '" + req.body.cognoms + "', data_naixament = '" + req.body.data_naixament + "', ubicacio = '" + req.body.ubicacio + "', user = '" + req.body.user + "', pass = '" + req.body.pass + "', descripcio = '" + req.body.descripcio + "' WHERE id = " + req.body.id + "");
    }
  });
  res.json(auth);
})


//Afegir un producte a la llista de megusta
app.post("/addMeGustaProduct", (req, res) => {
  let auth = true
  con.query("SELECT id_usuari, id_producte FROM MEGUSTA WHERE id_usuari = '" + req.body.id_usuari + "' and id_producte = '" + req.body.id_producte + "';", function (err, result, field) {
    if (result != 0) {
      auth = false;
      res.json(auth);
    } else {
      con.query("INSERT INTO MEGUSTA VALUES (NULL, " + req.body.id_usuari + ", " + req.body.id_producte + ", " + req.body.id_image_prod + ", '" + req.body.user_prod + "')");
      res.json(auth);
    }
  });
});

//Treure 1 de la llista
app.post("/deleteMeGustaProduct", (req, res) => {
  let auth = true
  con.query("SELECT id_usuari, id_producte FROM MEGUSTA WHERE id_usuari = '" + req.body.id_usuari + "' and id_producte = '" + req.body.id_producte + "';", function (err, result, field) {
    if (result != 0) {
      con.query("DELETE FROM MEGUSTA WHERE id_usuari = '" + req.body.id_usuari + "' and id_producte = '" + req.body.id_producte + "';");
      res.json(auth);
    } else {
      auth = false;
      res.json(auth);
    }
  });
});


//Registre PRODUCTE nou APP
app.post("/addNewProduct", (req, res) => {
  let auth = true;
  con.query("INSERT INTO PRODUCTE VALUES (NULL, '" + req.body.nom + "', " + req.body.preu + ", '" + req.body.categoria + "', 'Disponible', '" + req.body.descripcion + "', '" + req.body.id_usu + "', NULL)",function (err, result, fields) {
      auth = true;
  });
  res.json(auth);
});

//Dades generals user per la PANTALLA PRINCIPAL APP
app.get("/dadesUserLogin/:id_user", (req, res) => {
  let id = req.params.id_user;
  con.query("SELECT * FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload) WHERE PERSONA.id = '" + id + "'", function (err, result, fields) {
    res.send(result);
  });
})


//Informacio del producte seleccionat
app.get("/getInfoSelectedProduct/:id_producte", (req, res) => {
  let id_producte = req.params.id_producte;
  con.query("SELECT PRODUCTE.nom, PRODUCTE.preu, PRODUCTE.categoria, PRODUCTE.descripcion, PERSONA.user, CONCAT('http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server/',UPLOADS_PRODUCT.path) as path_prod, PERSONA.id, PRODUCTE.id_image FROM PRODUCTE JOIN PERSONA ON (PRODUCTE.id_usu = PERSONA.id) JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) WHERE PRODUCTE.id_producte = '" + id_producte + "'", function (err, result, fields) {
    res.send(result);
  });
})


//POST Afegir una peticio
app.post("/addNewPeticio", (req, res) => {
  con.query("INSERT INTO PETICIONES VALUES(" + req.body.id_usu + ", '" + req.body.peticion + "')", function (err, result, fields) {
    res.send(result);
  });
});





//GET IMAGE NAV BAR USER LOGIN
app.get('/imageUserLogin/:id_user', (req, res) => {
  var id = req.params.id_user;
  var options = {
      root: path.join(__dirname)
  };

  var filename = "";
  var fileName = "";
  con.query("SELECT UPLOADS.path FROM PERSONA JOIN UPLOADS ON (PERSONA.id_image = UPLOADS.id_upload) WHERE PERSONA.id = " + id + ";", function (err, result, fields) {
    filename = JSON.stringify(result);
    filename = filename.replace(/\[/g, "");
    filename = filename.replace(/\{/g, "");
    filename = filename.replace(/\}/g, "");
    filename = filename.replace(/\]/g, "");
    filename = filename.replace(/\"/g, "");
    filename = filename.replace(/path/, "");
    filename = filename.replace(/\:/g, "");
    filename = filename.replace(/.$/, "");
    var array = filename.split("\\".concat("\\"));
    
    fileName = "/uploads/user_images/".concat(array[2]);
    
    if (array[2] == null){
      //AQUI TORNEM A FER SPLIT perque la ruta a vegades es puja amb '\' i d'altres es puja amb '/'
      //i amb aixó fem test de la segona manera en cas que la primera no serveixi
      array = filename.split("\/");
      fileName = "/uploads/user_images/".concat(array[2]);
      if (array[2] == "normal_user_img.jp"){
        res.send("http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server/uploads/user_images/normal_user_img.jpg");
      } else {
        res.send("http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server" + fileName);
      }
    } else {
      res.send("http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server" + fileName);
    }    
  });
});

//Validació LOGIN a l'APP
app.get("/validarLogIn/:txtUserSignIn/:txtPasswordSignIn", (req, res) => {
  let user = req.params.txtUserSignIn;
  let passwd = req.params.txtPasswordSignIn;
  let auth = false;
  con.query(
    "SELECT * FROM PERSONA WHERE user = '" +
    user +
    "' && pass ='" +
    passwd +
    "'",
    function (err, result, fields) {
      if (result != 0) {
        res.send(JSON.stringify(result));
      } else {
        res.send(auth);
      }
    }
  );
});


//GET dades products per RECYCLE VIEW
app.get("/dadesProductsJSON", (req, res) => {
  con.query("SELECT PRODUCTE.id_producte, PRODUCTE.nom, PRODUCTE.preu, PRODUCTE.descripcion, PRODUCTE.categoria, CONCAT('http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server/',UPLOADS_PRODUCT.path) as path, PERSONA.user, PRODUCTE.estado_prod FROM PRODUCTE JOIN UPLOADS_PRODUCT ON (PRODUCTE.id_image = UPLOADS_PRODUCT.id_upload) JOIN PERSONA ON (PRODUCTE.id_usu = PERSONA.id) ORDER BY PRODUCTE.id_producte DESC", function(err, result, field){
    res.send(result);
  });
});


//GET dades dels products que te ME GUSTA de l'usuari
app.get("/getMeGustaProducts/:id_usuari", (req, res) => {
  let id_usuari = req.params.id_usuari;
  con.query("SELECT PRODUCTE.id_producte, PRODUCTE.nom, PRODUCTE.preu, PRODUCTE.descripcion, PRODUCTE.categoria, CONCAT('http://51.68.226.126/~a19teomerrod/2DAMA_Grup6/Backend/Server/',UPLOADS_PRODUCT.path) as path, MEGUSTA.user, PRODUCTE.estado_prod FROM MEGUSTA JOIN UPLOADS_PRODUCT ON (MEGUSTA.id_image_prod = UPLOADS_PRODUCT.id_upload) JOIN PRODUCTE ON (MEGUSTA.id_producte = PRODUCTE.id_producte) WHERE MEGUSTA.id_usuari = " + id_usuari + " ORDER BY PRODUCTE.id_producte;", function(err, result, field){
    res.send(result);
  });
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

app.post("/banear", (req, res) =>{
  if (req.body.values[1] == 1) {
    con.query("UPDATE PERSONA SET PERSONA.ban = '0' WHERE PERSONA.email = '" + req.body.values[0] + "'", function (err, result, field) {
      res.json(result);
    });
  } else {
    con.query("UPDATE PERSONA SET PERSONA.ban = '1' WHERE PERSONA.email = '" + req.body.values[0] + "'", function (err, result, field) {
      res.json(result);
    });
  }
});


app.post("/editUser", (req, res)=>{
  var email = req.body.values[0]; 
  var nom =  req.body.values[1];
  var cognoms = req.body.values[2]; 
  var data_naixament = req.body.values[3]; 
  var ubicacio = req.body.values[4];
  var user = req.body.values[5]; 
  var pass = req.body.values[6]; 
  var descripcio = req.body.values[7];
  var rol = req.body.values[8];
  var id = req.body.values[9];
  var id_image = req.body.values[10];
  var ban = req.body.values[11];
  con.query("UPDATE `PERSONA` SET `email`='" + email +"',`nom`='"+ nom +"',`cognoms`='"+cognoms+"',`data_naixament`='"+data_naixament+"',`ubicacio`='"+ubicacio+"',`user`='"+user+"',`pass`='"+pass+"',`descripcio`='"+descripcio+"',`rol`='"+rol+"',`id`='"+id+"',`id_image`='"+id_image+"',`ban`='"+ban+"'WHERE PERSONA.id = '" + id + "'", function (err, result, field) {
      res.json(result);
});
});


/* ---------------------- ENVIAR FITXERS A LA CARPETA USER ---------------------- */

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/user_images')
  },
  filename: function (req, file, cb) {
    con.query("SELECT IF((SELECT MAX(id) FROM PERSONA) = 1, (SELECT MAX(id + 1) as id FROM PERSONA), (SELECT MAX(id) as id FROM PERSONA)) as id;", function (err, result, field) {
      var emailNomFile = JSON.stringify(result);
      emailNomFile = emailNomFile.replace(/\[/g, "");
      emailNomFile = emailNomFile.replace(/\{/g, "");
      emailNomFile = emailNomFile.replace(/\"/g, "");
      emailNomFile = emailNomFile.replace(/\:/g, "_");
      emailNomFile = emailNomFile.replace(/\}/g, "_");
      emailNomFile = emailNomFile.replace(/\]/g, "_");
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
    con.query("UPDATE PERSONA SET id_image = (SELECT max(id_upload) FROM UPLOADS) where PERSONA.id = (SELECT MAX(id) FROM PERSONA)", function (err, result, field) {
      res.send({ code: 200, msg: file });
    });
  });
})


/* ---------------------- ENVIAR FITXERS A LA CARPETA PRODUCTE ---------------------- */

var storage = multer.diskStorage({
  destination: function (req, file, cb) {
    cb(null, 'uploads/product_images')
  },
  filename: function (req, file, cb) {
    con.query("SELECT id_producte FROM `PRODUCTE` WHERE id_producte = (SELECT MAX(id_producte) FROM PRODUCTE);", function (err, result, field) {
      var productName = JSON.stringify(result);
      productName = productName.replace(/\[/g, "");
      productName = productName.replace(/\{/g, "");
      productName = productName.replace(/\"/g, "");
      productName = productName.replace(/\:/g, "_");
      productName = productName.replace(/\}/g, "_");
      productName = productName.replace(/\]/g, "_");
      cb(null, file.fieldname + '_' + productName + '.jpg')
    });
  }
})

var upload2 = multer({ storage: storage })

app.post("/uploadProductImage", upload2.single('myFile'), (req, res) => {
  const file = req.file
  if (!file) {
    const error = new Error('Please upload a file')
    error.httpStatusCode = 400
    console.log("error", 'Please upload a file');

    res.send({ code: 500, msg: 'Please upload a file' })
    return next({ code: 500, msg: error })
  }

  req.file.path.replace(/\"/g, "");
  con.query("INSERT INTO UPLOADS_PRODUCT VALUES (null, " + JSON.stringify(req.file.path) + ")", function (err, result, field) {
    con.query("UPDATE PRODUCTE SET PRODUCTE.id_image = (SELECT max(id_upload) FROM UPLOADS_PRODUCT) where PRODUCTE.id_producte = (SELECT MAX(id_producte) FROM PRODUCTE)", function (err, result, field) {
      res.send({ code: 200, msg: file });
    });
  });
})

/*Obrir Servidor*/
app.listen(PORT, () => {
  console.log("Server RUNNING [" + PORT + "]");
});
