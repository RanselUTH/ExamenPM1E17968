package com.example.examenpm1e17968;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.examenpm1e17968.Tablas.Paises;
import com.example.examenpm1e17968.Transacciones.Transacciones;

import java.util.ArrayList;




public class ActivityEdit extends AppCompatActivity {


    private String idCont, nombre, telefono, pais, notas;
    EditText txtbuscarid, txtNombre, txtTelefono, txtNota;
    Spinner combobuscarpais;
    SQLiteConexion conexion;
    ArrayList<String> listaPaises;
    ArrayList<Paises> paises;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        conexion = new SQLiteConexion(this, Transacciones.NameDataBase, null, 1);

        Button btnEliminarContacto = (Button) findViewById(R.id.btneliminar);
        Button btnActualizarContacto = (Button) findViewById(R.id.btnactualizar);

        txtbuscarid = (EditText) findViewById(R.id.txtIdContactoBuscado);
        txtNombre = (EditText) findViewById(R.id.txtNombreModificar);
        txtTelefono = (EditText) findViewById(R.id.txtTelefonoModificar);
        txtNota = (EditText) findViewById(R.id.txtInformacionModificar);
        combobuscarpais = (Spinner) findViewById(R.id.cmbPaisesBuscado);


        ObtenerListaPaises();
        ArrayAdapter<CharSequence> adp = new ArrayAdapter(this, android.R.layout.simple_spinner_item, listaPaises);
        combobuscarpais.setAdapter(adp);

        Intent intent = getIntent();
        idCont = intent.getStringExtra("idCont");
        txtbuscarid.setText(idCont);
        txtbuscarid.setKeyListener(null);
        BuscarContacto();

        btnEliminarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EliminarContacto();
            }
        });

        btnActualizarContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActualizarContacto();
            }
        });
    }

    private void BuscarContacto() {
        SQLiteDatabase db = conexion.getWritableDatabase();
        String [] params = {idCont};
        String [] fields = {Transacciones.nombreContacto,
                Transacciones.telefonoContacto,
                Transacciones.pais,
                Transacciones.nota};
        String wherecond = Transacciones.idContacto + "=?";

        try{
            Cursor cdata = db.query(Transacciones.tablaContactos, fields, wherecond, params, null, null, null);
            cdata.moveToFirst();

            txtNombre.setText(cdata.getString(0));
            txtTelefono.setText(cdata.getString(1));
            combobuscarpais.setSelection(Integer.valueOf(cdata.getString(2)) - 1);
            txtNota.setText(cdata.getString(3));

        }catch (Exception ex){
            Toast.makeText(getApplicationContext(), "Elemento no encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    private void ActualizarContacto() {
        int numeros = 0;
        if(txtNombre.getText().toString().isEmpty() || txtTelefono.getText().toString().isEmpty()) {
            mostrarDialogoVacios();
        } else {
            for (int i = 0; i < txtNombre.getText().toString().length(); i++) {
                if (Character.isDigit(txtNombre.getText().toString().charAt(i))) {
                    mostrarDialogoNumeros();
                    numeros = 1;
                    break;
                }
            }

            if (numeros == 0) {
                SQLiteDatabase db = conexion.getWritableDatabase();
                String [] params = {idCont}; //Parametro de Busqueda

                ContentValues valores = new ContentValues();
                valores.put(Transacciones.nombreContacto, txtNombre.getText().toString());
                valores.put(Transacciones.telefonoContacto, txtTelefono.getText().toString());
                valores.put(Transacciones.pais, (combobuscarpais.getSelectedItemId() + 1));
                valores.put(Transacciones.nota, txtNota.getText().toString());

                db.update(Transacciones.tablaContactos, valores, Transacciones.idContacto + "=?", params);
                Toast.makeText(getApplicationContext(), "Contacto Actualizado", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), ActivityListContactos.class);
                startActivity(intent);
            }
        }

    }

    private void EliminarContacto() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar la Eliminación")
                .setMessage("¿Desea eliminar este contacto de " + txtNombre.getText() + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SQLiteDatabase db = conexion.getWritableDatabase();
                        String [] params = {idCont}; //Parametro de Busqueda

                        db.delete(Transacciones.tablaContactos, Transacciones.idContacto + "=?", params);
                        Toast.makeText(getApplicationContext(), "Contacto Eliminado", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), ActivityListContactos.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "eliminación cancelada", Toast.LENGTH_LONG).show();
                    }
                }).show();
    }

    private void ObtenerListaPaises() {
        SQLiteDatabase db = conexion.getReadableDatabase();
        Paises pais = null;

        paises = new ArrayList<Paises>();

        Cursor cursor = db.rawQuery("SELECT * FROM " + Transacciones.tablaPaises, null);

        while (cursor.moveToNext()){
            pais = new Paises();
            pais.setIdPais(cursor.getInt(0));
            pais.setNombrePais(cursor.getString(1));
            pais.setCodigoMarcado(cursor.getInt(2));

            paises.add(pais);
        }

        fillComb();
        cursor.close();
    }

    private void fillComb() {
        listaPaises = new ArrayList<String>();
        for(int i = 0; i < paises.size(); i++){
            listaPaises.add(paises.get(i).getNombrePais() + "  ("
                    + paises.get(i).getCodigoMarcado() + ")");
        }
    }

    private void mostrarDialogoVacios() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Vacíos")
                .setMessage("No puede dejar ningún campo vacío")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }

    private void mostrarDialogoNumeros() {
        new AlertDialog.Builder(this)
                .setTitle("Alerta de Números")
                .setMessage("No puede ingresar números en el campo de Nombre")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}