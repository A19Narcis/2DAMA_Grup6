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
    con.query("SELECT * FROM PERSONA", function(err, result, fields){
        if (err) throw err;
        console.log(result);
        res.json(result);
    });
});

app.post("/getAdmins", (req, res) =>{
    var dades = [];
    con.query("SELECT * FROM PERSONA", function(err, result, fields){
        dades = result;
        res.json(dades);
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