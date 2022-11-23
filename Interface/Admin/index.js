var app = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    data() {
        return {
            text: [],
            headers: [
                { text: 'ID', value: 'id' },
                { text: 'NOM', align: 'start', sortable: true, value: 'nom' },
                { text: 'COGNOMS', value: 'cognoms' }, { text: 'EDAD', value: 'data_naixament' },
                { text: 'EMAIL', value: 'email' }, { text: 'USERS', value: 'users' },
                { text: 'PASSWORD', value: 'pass' }, { text: 'ROL', value: 'rol' },
                { text: 'BAN', value: 'ban' }, { text: 'INFO' }, { text: 'BAN' }
            ],
            search: '', h: 0, speed: 2, w: 0,
            game: 0, bottle_width: 0, bottle_height: 0,
            bottle_radius: 0, bottle_x: 0, bottle_y: 0,
            move_bottle_x: 0, move_bottle_y: 0, canv: null,
            edit: 0, snackbar: false, teo: "Hola fumo porros",
            timeout: 1500,img_usu: '', arrEdit: [],arrPet: [],isLock: false,
            lat: "",long: "",login: 0,s2rc: "",prod: 0,isUser: 0,
            isArtista: 0,usuinfo: [],logout: 0,sheet: false, users: [],
            seePr: [],img_prod: ' ',dial: 0,img: ' ',seeUs: [],
            dialog: false,dialog2: false,isAdmin: 0,showPassword: false,
            password: null,info: { values: [] },error: ' ',err: ' ',products: [],
        }
    },
    mounted() {
        var canvas = document.getElementById('game_canvas');
        this.w = canvas.width;
        this.h = canvas.height;
        var ctx = canvas.getContext('2d');
        this.canv = ctx;
    },
    methods: {
        mover() {
            if (this.move_bottle_x == 0)
                this.move_bottle_x = this.w;
            this.canv.fillStyle = "#F1F1F1";
            this.canv.fillRect(0, 0, this.w, this.h);
            this.draw(this.bottle_x, this.bottle_y, '#E8E8E8');
            this.draw(this.move_bottle_x, this.move_bottle_y, '#AFDBFF');
            this.move_bottle_x--;
        },
        draw(x, y, fillColor) {
            width = this.bottle_width, height = this.bottle_height, radius = this.bottle_radius;
            this.canv.fillStyle = fillColor;
            this.canv.strokeStyle = 'black';
            this.canv.lineWidth = 4;
            this.canv.beginPath();
            this.canv.moveTo(x, y + radius);
            this.canv.lineTo(x, y + height - radius);
            this.canv.arcTo(x, y + height, x + radius, y + height, radius);
            this.canv.lineTo(x + width - radius, y + height);
            this.canv.arcTo(x + width, y + height, x + width, y + height - radius, radius);
            this.canv.lineTo(x + width, y + radius);
            this.canv.quadraticCurveTo((x + width) - 25, y - (height - 25) + 65, (x + width) - 10, y - (height - 40));
            this.canv.quadraticCurveTo(x + (width / 2), y - (height - 40), x + 10, y - (height - 40));
            this.canv.quadraticCurveTo(x + 25, y - (height - 25) + 65, x, y + radius + 1);
            this.canv.stroke();
            this.canv.fill();
        },
        stop() {
            if (typeof game_loop != "undefined") clearInterval(game_loop);
            if (this.bottle_x == this.move_bottle_x || this.bottle_x == (this.move_bottle_x - 2) || (this.bottle_x == this.move_bottle_x + 2))
                this.teo = 'Ganaste';
            else
                this.teo = 'Has perdido';
        },
        start() {
            this.bottle_width = 60;
            this.bottle_height = 100;
            this.bottle_radius = 20;
            this.bottle_x = (this.w - this.bottle_width) / 2;
            this.bottle_y = this.h - (this.bottle_height + 30);
            this.move_bottle_x = this.w;
            this.move_bottle_y = this.h - (this.bottle_height + 30);
            this.move_bottle_x = this.w, this.move_bottle_y = this.h - (this.bottle_height + 30);
            if (typeof game_loop != "undefined") clearInterval(game_loop);
            game_loop = setInterval(this.mover, this.speed);
        },
        getUsers: function (data) {
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.users = data;
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        getAdmins: function (data) {
            this.info.values.push(document.getElementById("user").value);
            this.info.values.push(document.getElementById("pass").value);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getAdmins/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    for (let index = 0; index < data.length; index++)
                        if (data[index].user == this.info.values[0] && data[index].pass == this.info.values[1] && data[index].rol == "admin") {
                            this.err = ""
                            this.logout = 1;
                            this.info.values = [];
                            this.usuinfo = data[index];
                            var str = "../../Backend/Server/";
                            str = str + this.usuinfo.path;
                            str = str.replaceAll('"', '');
                            this.usuinfo.path = str;
                            this.img_usu = this.usuinfo.path;
                            this.login = 1;
                            this.$refs.inputRef.reset();
                            this.$refs.inputRef2.reset();
                            return;
                        } else {
                            this.err = "Usuario y/o contraseña incorrectos"
                        }
                    this.info.values = [];
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        getProducts: function () {
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.login = 0;
                    this.prod = 1;
                    this.products = data;
                    var str = "../../Backend/Server/";
                    str = str + this.products[1].path;
                    this.img_prod = str;
                    this.info.values = []
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        seeProduct: function (id) {
            this.info.values.push(id);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/seeProduct/",
                {
                    method: "POST",
                    headers: {
                        'Content-Type': 'application/json',
                        'Accept': 'application/json',
                    },
                    body: JSON.stringify(this.info),
                    mode: "cors",
                    cache: "default"
                }
            ).then(
                (response) => {
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.seePr = data[0];
                    console.log(this.seePr.path);
                    this.info.values = [];
                    this.sheet = true;
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        editUser: function (email, nom, cognoms, data_naixament, ubicacio, user, pass, descripcio, rol, id, id_image, ban) {
            this.info.values.push(email);
            this.info.values.push(nom);
            this.info.values.push(cognoms);
            this.info.values.push(data_naixament);
            this.info.values.push(ubicacio);
            this.info.values.push(user);
            this.info.values.push(pass);
            this.info.values.push(descripcio);
            this.info.values.push(rol);
            this.info.values.push(id);
            this.info.values.push(id_image);
            this.info.values.push(ban);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/editUser/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.info.values = [];
                    app.getUsers();
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        banear: function (email, ban, rol) {
            if (rol == 'admin') {
                return;
            }
            this.info.values.push(email);
            this.info.values.push(ban);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/banear/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.info.values = [];
                    app.getUsers();

                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        seeUsers: function (email) {
            this.info.values.push(email);
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.seeUs = data[0];
                    var str = "../../Backend/Server/";
                    str = str + this.seeUs.path;
                    str = str.replaceAll('"', '');
                    this.seeUs.path = str;
                    this.img = this.seeUs.path;
                    var str = [];
                    str = this.seeUs.ubicacio.split(' ');
                    this.lat = str[0];
                    this.long = str[1];
                    this.s2rc = "https://www.google.com/maps/embed/v1/place?key=AIzaSyCGBVNGL-01rb2enDljAeYFQE-elNlu2RI &q=" + this.lat + ',' + this.long + "&zoom=18";
                    this.info.values = [];
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        decidirPeticion: function (user, decision, id) {
            this.info.values.push(user);
            this.info.values.push(decision);
            this.info.values.push(id);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/decidirPeticion/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.info.values = [];
                    app.getUsers();
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        seeEdit: function (id) {
            this.info.values.push(id);
            const myHeaders = new Headers();
            fetch("http://localhost:3000/seeEdit/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.arrEdit = data[0];
                    var str = "../../Backend/Server/";
                    str = str + this.arrEdit.path;
                    str = str.replaceAll('"', '');
                    this.arrEdit.path = str;
                    this.img = this.arrEdit.path;
                    this.info.values = [];
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        },
        seePeticiones: function () {
            const myHeaders = new Headers();
            fetch("http://localhost:3000/seePeticiones/",
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
                    return (response.json());
                }
            ).then(
                (data) => {
                    this.arrPet = data;
                }
            ).catch(
                (error) => {
                    console.log(error);
                }
            );
        }
    }
})