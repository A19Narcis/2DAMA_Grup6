var app = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    data: {
        text: [
        ],
        info: {values: []}
    },
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
                    this.text = data;
                     
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
                    this.text = data;
                     
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