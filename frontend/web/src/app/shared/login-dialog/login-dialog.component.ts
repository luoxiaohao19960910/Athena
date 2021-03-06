import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material';
import {LoginService} from '../../core/service/login.service';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {AuthService} from '../../core/service/auth.service';
import {NgForm} from "@angular/forms";
import {ActivatedRoute, Params, Router} from '@angular/router';

@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.scss']
})
export class LoginDialogComponent implements OnInit {
  ifHidePassword: boolean = true;
  isLoginPage: boolean = false;
  loginFailMsg: string = '';
  redirectTo: string = null;

  constructor(public dialogRef: MatDialogRef<LoginDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: any, private loginService: LoginService, private authService: AuthService,
              private router: Router) {
    if (this.data && this.data["isLoginPage"]) {
      this.isLoginPage = this.data["isLoginPage"];
    }
    if (this.data && this.data["redirectTo"]) {
      this.redirectTo = this.data["redirectTo"];
    }
  }

  ngOnInit() {
  }

  onSubmit(form: NgForm): void {
    const formValue = form.value;
    this.loginService.login(formValue['id'], formValue['password'])
      .subscribe({
        next: (response: HttpResponse<any>) => {
          this.loginFailMsg = '';
          if (this.isLoginPage) {
            // if is login page
            this.dialogRef.close();
            if (this.redirectTo) {
              this.router.navigate([this.redirectTo]);
            }
            else {
              this.router.navigate(["/"])
            }
          }
          else {
            this.dialogRef.close();
          }
        },
        error: (error: HttpErrorResponse) => {
          if (error.status < 500 && error.status > 400) {
            this.loginFailMsg = "Id or Password Error";
          }
          else {
            this.loginFailMsg = "Bad network, please try again later";
          }
          for (let name in form.controls) {
            form.controls[name].reset();
          }
        },
      });
  }

  onNoClick(): void {
    this.dialogRef.close();
  }
}
