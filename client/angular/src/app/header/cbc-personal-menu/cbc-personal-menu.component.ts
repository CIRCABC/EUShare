import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cbc-personal-menu',
  templateUrl: './cbc-personal-menu.component.html',
  styleUrls: ['./cbc-personal-menu.component.scss']
})
export class CbcPersonalMenuComponent implements OnInit {

  public useEULogin = true;
  public user = {
    firstname: 'hello',
    lastname: 'world',
    avatar: 'doesntmatter'
  }
  constructor() { }

  ngOnInit(): void {
  }

  euLogin() {

  }
  isGuest() {

  }

  isUser() {
    return true;
  }

  isAppAdmin() {

  }

}
