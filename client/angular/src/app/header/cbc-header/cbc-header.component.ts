import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-cbc-header',
  templateUrl: './cbc-header.component.html',
  styleUrls: ['./cbc-header.component.scss']
})
export class CbcHeaderComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

  isGuest() {
    return true;
  }

}
