class Estudiante {

    constructor(nombre, edad) {
        this.nombre = nombre;
        this.edad = edad;
        this.notas = notas;
    }

    promedio() {
        const total = this.notas.reduce((acumulador, nota) => acumulador + nota, 0);
        const resultado = total / this.notas.length;
        console.log(resultado);
    }
}

new Estudiante = {
    nombre: "Juan",
    edad: 25,
    notas: [7,9,9,10],
};

Estudiante.promedio();