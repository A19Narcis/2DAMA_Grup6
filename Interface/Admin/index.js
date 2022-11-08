var app = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    data() {
        return {
            headers: [
                {
                    text: 'NOM',
                    align: 'start',
                    sortable: true,
                    value: 'nom',
                },
                { text: 'COGNOMS', value: 'cognoms' },
                { text: 'EDAD', value: 'data_naixament' },
                { text: 'EMAIL', value: 'email' },
                { text: 'UBICACIO', value: 'ubicacio' },
                { text: 'PASSWORD', value: 'pass' },
                { text: 'ROL', value: 'rol' },
                { text: 'INFO' },
                { text: 'ADMIN/USER' }
            ],
            text: [],
            login: 0,
            prod: 0,
            users: [],
            dial: 0,
            img: ' ',
            seeUs: [],
            isadmin: 4,
            info: { values: [] },
            err: ' '
        }
    },
    methods: {
        getUsers: function (data) {
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getUsers/",
                {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    mode: "cors",
                    cache: "default"
                }
            ).then(
                (response) => {
                    //console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    //onsole.log(data);
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
            fetch("http://localhost:3000/getUsers/",
                {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    mode: "cors",
                    body: JSON.stringify(this.info),
                    cache: "default"
                }
            ).then(
                (response) => {
                    //console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    //console.log(data);
                    for (let index = 0; index < data.length; index++)
                        if (data[index].user == this.info.values[0] && data[index].pass == this.info.values[1] && data[index].rol == "admin") {
                            console.log("hola");
                            this.isadmin = 1;
                            this.info.values = [];
                            this.login = 1;
                            return;
                        }
                    this.info.values = [];
                    this.isadmin = 0;
                    this.err = "Correo y/o contraseña incorrectos"
                }
            ).catch(
                (error) => {
                    console.log("Error. ");
                    console.log(error);
                }
            );
        },

        getProducts: function (data) {
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getProducts/",
                {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    mode: "cors",
                    cache: "default"
                }
            ).then(
                (response) => {
                    //console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    //console.log(data);
                    this.login = 0;
                    this.prod = 1;
                    this.products = data[0];

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
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    mode: "cors",
                    body: JSON.stringify(this.info),
                    cache: "default"
                }
            ).then(
                (response) => {
                    //console.log(response);
                    return (response.json());
                }
            ).then(
                (data) => {
                    //console.log(data);
                    this.seeUs = data[0];
                    var str = "../../Backend/Server/";
                    str = str + this.seeUs.path;
                    str = str.replaceAll('"', '');
                    this.seeUs.path = str;
                    this.img = this.seeUs.path;
                    //console.log(this.img);
                    data = [];
                    this.info.values = [];


                }
            ).catch(
                (error) => {
                    console.log("Error. ");
                    console.log(error);
                }
            );
        },

        ferAdmin: function (email, rol) {
            if (confirm("Estas seguro de cambiar el rol a " + email) == true) {
                this.info.values.push(email);
                this.info.values.push(rol);
                console.log(this.info.values);
                console.log(email);
                console.log("Get Data");
                const myHeaders = new Headers();
                fetch("http://localhost:3000/ferAdmin/",
                    {
                        method: "POST",
                        headers: {
                            'Content-Type': 'application/json',
                            'Accept': 'application/json',
                        },
                        mode: "cors",
                        body: JSON.stringify(this.info),
                        cache: "default"
                    }
                ).then(
                    (response) => {
                        //onsole.log(response);
                        return (response.json());
                    }
                ).then(
                    (data) => {
                        //console.log(data);
                        this.users = data[0];
                        this.info.values = [];
                        app.getUsers();
                    }
                ).catch(
                    (error) => {
                        console.log("Error. ");
                        console.log(error);
                    }
                );
            }

        },
        onClickImage() {
            alert('Clicked image')
        },
    }
});

