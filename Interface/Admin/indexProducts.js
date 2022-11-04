var app2 = new Vue({
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
               { text: 'PREU', value: 'preu' },
               { text: 'CATEGORIA', value: 'categoria' },
               { text: 'ESTADO', value: 'estado_prod' },
               { text: 'DESCRIPCIO', value: 'descripcio' },
               { text: 'EMAIL', value: 'correu_usu' },
               { text: 'VER'}
             ],
           search: '',
           products: [ ],
           seePr: [ ],
           dialog: false,
           isadmin: 4,
           showPassword: false,
           password: null,
           info: {values: []},
           error: ' ',
           err: ' '        
       }},     
    methods: {
        getProducts: function (data) {   
            console.log("Get Data");
            const myHeaders = new Headers();
            fetch("http://localhost:3000/getProducts/",
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
                    this.seePr = data[0];
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
