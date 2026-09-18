const palabras = ["casa", "ana", "sol", "radar", "javascript", "asa"];


// 1. Contar palabras con más de 4 caracteres
const masDeC = palabras.filter(palabra => palabra.length > 4);
console.log("Palabras con más de 4 caracteres: " + masDeC.length);
console.log("Cuáles son: " + masDeC.join(", "));

// 2. String con todas las palabras unidas por "-"
const unidas = palabras.join("-");
console.log("Palabras unidas: " + unidas);