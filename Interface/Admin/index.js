var app = new Vue({
    el: "#app",
    vuetify: new Vuetify(),
    data(){
        return{
         text: [
        ],
        headers: [
            {
              text: 'Nom',
              align: 'start',
              sortable: false,
              value: 'name',
            },
            { text: 'cognoms', value: 'cognoms' },
            { text: 'edad', value: 'edad' },
            { text: 'email', value: 'email' },
            { text: 'ubicacio', value: 'ubicacio' },
            { text: 'pass', value: 'pass' },
          ],
        users: [ {
            name: 'Frozen Yogurt',
            calories: 159,
            fat: 6.0,
            carbs: 24,
            protein: 4.0,
            iron: '1%',
          },
          {
            name: 'Ice cream sandwich',
            calories: 237,
            fat: 9.0,
            carbs: 37,
            protein: 4.3,
            iron: '1%',
          },],
        isadmin: 0,
        info: {values: []},
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
                    this.text = (data);
                     
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
            this.isAdmin = 0;
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
                    if (this.info.values[0] == data[index].nom && this.info.values[1] == data[index].pass)
                    {
                        this.isadmin = 1;
                        window.open("./Users.html","_self");
                        return ;
                    }
                    else 
                        this.isadmin = 0;
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

