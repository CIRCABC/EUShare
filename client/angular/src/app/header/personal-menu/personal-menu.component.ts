import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-personal-menu',
  templateUrl: './personal-menu.component.html',
  styleUrls: ['./personal-menu.component.scss']
})
export class PersonalMenuComponent implements OnInit {

  public dropdownActive = false;

  constructor() { }

  click() {
    this.dropdownActive = !this.dropdownActive;
  }
  
  ngOnInit(): void {
  }

}
