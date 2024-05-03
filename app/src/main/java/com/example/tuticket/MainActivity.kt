package com.example.tuticket

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tuticket.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adaptador: AdaptadorUsuario

    var listaUsuarios = arrayListOf<Usuario>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        llenarLista()
        setupRecyclerView()

        binding.etBuscador.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }

            override fun afterTextChanged(p0: Editable?) {
                filtrar(p0.toString())
            }

        })

        // Ocultar el RecyclerView
        binding.rvLista.visibility = View.GONE

        // Acceder a Firestore
        val db = FirebaseFirestore.getInstance()

        // Array de ImageViews
        val imageViews = arrayOf(
            findViewById<ImageView>(R.id.imageView8),
            findViewById<ImageView>(R.id.imageView9),
            findViewById<ImageView>(R.id.imageView10),
            findViewById<ImageView>(R.id.imageView11),
            findViewById<ImageView>(R.id.imageView12),
            findViewById<ImageView>(R.id.imageView13)
        )

        // Recuperar la información de Firestore para los otros ImageViews
        db.collection("eventos")
            .limit(6)
            .get()
            .addOnSuccessListener { documents ->
                documents.forEachIndexed { index, document ->
                    val imageUrl = document.getString("imagenEvento")
                    // Cargar la imagen desde la URL y mostrarla en un ImageView
                    imageUrl?.let {
                        Picasso.get().load(it).into(imageViews[index])
                        // Configurar OnClickListener para cada ImageView
                        imageViews[index].setOnClickListener {
                            val intent = Intent(this, DetalleEventoActivity::class.java).apply {
                                putExtra("eventoId", document.id)
                                putExtra("imagenUrl", imageUrl)
                                putExtra("nombreEvento", document.getString("nombreEvento"))
                                putExtra("fechaEvento", document.getString("fechaEvento"))
                                putExtra("horaEvento", document.getString("horaEvento"))
                                putExtra("ubicacionEvento", document.getString("ubicacionEvento"))
                            }
                            startActivity(intent)
                        }
                    }
                }
            }
    }

    fun llenarLista() {
        listaUsuarios.add(Usuario("GALACTIC EMPIRE", "15 DE OCTUBRE", "MOVISTAR ARENA"))
        listaUsuarios.add(Usuario("ANDRES CEPEDA", "13 DE DICIEMBRE", "MOVISTAR ARENA"))
        listaUsuarios.add(Usuario("BICEP DJ SET", "11 DE MAYO", "ROYAL CENTER"))
        listaUsuarios.add(Usuario("MESSI10", "10 DE JUNIO", "SIMON BOLIVAR"))
        listaUsuarios.add(Usuario("BLACK PUMAS", "22 DE JUNIO", "CAMPIN"))
    }

    fun setupRecyclerView() {
        binding.rvLista.layoutManager = LinearLayoutManager(this)
        adaptador = AdaptadorUsuario(listaUsuarios)
        binding.rvLista.adapter = adaptador
    }

    fun filtrar(texto: String) {
        var listaFiltrada = arrayListOf<Usuario>()

        listaUsuarios.forEach {
            if (it.nombre.toLowerCase().contains(texto.toLowerCase())) {
                listaFiltrada.add(it)
            }
        }

        adaptador.filtrar(listaFiltrada)
    }
}






