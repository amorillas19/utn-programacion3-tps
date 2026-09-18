const palabras = ["casa", "sol", "mesa", "programación", "javascript"];

console.log("=== INVERSIÓN DE PALABRAS ===\n");
console.log("Array original:", palabras);

// Opción 1: Con métodos funcionales (más compacto)
const palabrasInvertidas = palabras.map(palabra => 
  palabra.split('').reverse().join('')
);

console.log("\nArray invertido:", palabrasInvertidas);

// Mostrar en alert (formateado)
alert("Palabras invertidas:\n" + palabrasInvertidas.join('\n'));

console.log("\n");

// Opción 2: Con loop manual (si querés ver más detalles)
console.log("Comparación lado a lado:");
for (let i = 0; i < palabras.length; i++) {
  console.log(`${palabras[i]} → ${palabrasInvertidas[i]}`);
}