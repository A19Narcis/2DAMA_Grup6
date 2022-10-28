var app = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    data(){
        return{
         text: [
        ],
        headers: [
            {
              text: 'NOM',
              align: 'start',
              sortable: true,
              value: 'nom',
            },
            { text: 'COGNOMS', value: 'cognoms' },
            { text: 'EDAD', value: 'edad' },
            { text: 'EMAIL', value: 'email' },
            { text: 'UBICACIO', value: 'ubicacio' },
            { text: 'PASSWORD', value: 'pass' },
            { text: 'ROL', value: 'rol'},
            { text: 'VER'}
          ],
        search: '',
        users: [ ],
        seeUs: [ ],
        dialog: false,
        isadmin: 4,
        showPassword: false,
        password: null,
        info: {values: []},
        error: ' ',
        err: ' '
    }},
    methods: {
        getUsers: function (data) {   
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getUsers/",
            {
                method: "POST",
                headers: {
                'Content-Type':'application/json',
                'Accept':'application/json',
                },
                mode: "cors",
                cache: "default"
            }
            ).then(
                (response) =>{
                   console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    console.log(data);
                    this.users = data;
                     
                }
            ).catch(
                (error) => {
                    console.log("Error. ");
                    console.log(error);
                }
            );
        },

        getAdmins: function (data) {
            this.info.values.push(document.getElementById("user").value);
            this.info.values.push(document.getElementById("pass").value);
            console.log(this.info.values);
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getAdmins/",
            {
                method: "POST",
                headers: {
                'Content-Type':'application/json',
                'Accept':'application/json',
                },
                mode: "cors",
                body: JSON.stringify(this.info),
                cache: "default"
            }
            ).then(
                (response) =>{
                   console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    console.log(data);
                    for (let index = 0; index < data.length; index++) 
                    if (this.info.values[0] == data[index].nom && this.info.values[1] == data[index].pass && data[index].rol == "admin")
                    {
                        this.isadmin = 1;
                        this.info.values = [];
                        window.open("./Users.html","_self");
                        return ;
                    }
                    else 
                    {
                        this.info.values = [];
                        this.isadmin = 0;        
                        this.err = "Correo y/o contraseña incorrectos"
                    }
                }
            ).catch(
                (error) => {
                    console.log("Error. ");
                    console.log(error);
                }
            );
        },

        seeUsers: function (email) {
            this.info.values.push(email);
            console.log(this.info.values);  
            console.log(email);
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/seeUsers/",
            {
                method: "POST",
                headers: {
                'Content-Type':'application/json',
                'Accept':'application/json',
                },
                mode: "cors",
                body: JSON.stringify(this.info),
                cache: "default"
            }
            ).then(
                (response) =>{
                   console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    
                    this.seeUs = data[0];
                    this.info.values = [];
                    
                }
            ).catch(
                (error) => {
                    console.log("Error. ");
                    console.log(error);
                }
            );
        },
    }
});

