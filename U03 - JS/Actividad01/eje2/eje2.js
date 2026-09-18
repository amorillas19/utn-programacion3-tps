const palabras = [];

for (let i = 0; i < 5; i++) {
  const palabra = prompt(`Ingresa la palabra ${i + 1}:`);
  palabras.push(palabra);
}

console.log("Array original:", palabras);

// Agregar al inicio
const nuevaPalabraInicio = prompt("Palabra para agregar al inicio:");
palabras.unshift(nuevaPalabraInicio);
console.log("Después de unshift:", palabras);

// Agregar al final
const nuevaPalabraFinal = prompt("Palabra para agregar al final:");
palabras.push(nuevaPalabraFinal);
console.log("Después de push:", palabras);

// Eliminar segunda palabra (índice 1)
palabras.splice(1, 1);
console.log("Después de eliminar índice 1:", palabras);