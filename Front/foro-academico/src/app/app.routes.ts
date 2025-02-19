import { RouterModule, Routes } from '@angular/router';
import { NgModule } from '@angular/core';
import { foroComponent } from './pages/foro/foro.component';
import { LoginComponent } from './pages/login/login.component';
import { RegistroComponent } from './pages/registro/registro.component';

export const routes: Routes = [
    { path: '', component: foroComponent },
    { path: 'login', component: LoginComponent },
    { path: 'registro', component: RegistroComponent },
]

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})

export class AppRoutingModule { }
