import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.css'] 
})
export class Login {

  onSubmit() {
    console.log("Formulario enviado");
  }

  loginWithFacebook() {
    console.log("Login con Facebook");
  }

  loginWithGoogle() {
    console.log("Login con Google");
  }

}

