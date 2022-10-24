const cors = require("cors");
const express = require("express");
const fs = require("fs");
const mysql = require('mysql2/promise');

const app = express();
const PORT = 3000;

app.use(express.json());
app.use (cors({
    origin: function (origin, callback){
        return callback(null, true);
    }
}));


var con = await mysql.createConnection({
    host: "labs.inspedralbes.cat",
    user: "a19teomerrod_user",
    password: "Pedralbes22_23",
    database: "a19teomerrod_project"
});

await con.connect(function(err){
    if (err)
        throw err;
    else{
        console.log("Connexio establerta");
    }
});





app.post("/getUsers", (req, res) =>{
    var data = [];
    con.query("SELECT * FROM PRODUCTE, PERSONA WHERE PERSONA.email = PRODUCTE.correu_usu", function(err, result, fields){
        if (err) throw err;
        data.push(result);
        res.json(data);
    });
});

function checkUser(datosUsu)
{
     const rows = await con.query("SELECT nom FROM PERSONA");
    for (let index = 0; index < rows.length; index++) 
        if (datosUsu[0][0] == result[index].nom)
            return 1;
        else 
            return 0;

}


function checkPass(datosUsu)
{
     const rows = await con.query("SELECT nom FROM PERSONA");
    for (let index = 0; index < rows.length; index++) 
        if (datosUsu[0][1] == result[index].pass)
            return 1;
        else 
            return 0;

}


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