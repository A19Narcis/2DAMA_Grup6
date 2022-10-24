const cors = require("cors");
const express = require("express");
const fs = require("fs");
const mysql = require('mysql2');

const app = express();
const PORT = 3000;

app.use(express.json());
app.use (cors({
    origin: function (origin, callback){
        return callback(null, true);
    }
}));


var con = mysql.createConnection({
    host: "labs.inspedralbes.cat",
    user: "a19teomerrod_user",
    password: "Pedralbes22_23",
    database: "a19teomerrod_project"
});

con.connect(function(err){
    if (err)
        throw err;
    else{
        console.log("Connexio establerta");
    }
});





app.post("/getUsers", (req, res) =>{
    var data = [];
    con.query("SELECT * FROM ADMIN JOIN PERSONA USING (email)WHERE PERSONA.email = email", function(err, result, fields){
        if (err) throw err;
        data.push(result);
        res.json(data);
    });
});


app.post("/getProducts", (req, res) =>{
    var data = [];
    con.query("SELECT * FROM `PERSONA`, `PRODUCTE` WHERE PRODUCTE.correu_usu=PERSONA.email;", function(err, result, fields){
        if (err) throw err;
        data.push(result);
        res.json(data);
    });
});


//Filtra per usuari els productes que es demanen
app.post("/getProductUser", (req, res) =>{
    var data = [];
    var emailUser="x@i";
    con.query("SELECT nom FROM PRODUCTE WHERE correu_usu='"+emailUser+"';", function(err, result, fields){
        if (err) throw err;
        data.push(result);
        res.json(data);
    });
});


/*Obrir Servidor*/
app.listen(PORT, () => {
    console.log("Server RUNNING ["+PORT+"]");
});