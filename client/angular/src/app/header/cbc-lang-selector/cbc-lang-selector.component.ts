import { Component, OnInit, Input } from '@angular/core';

@Component({
  selector: 'app-cbc-lang-selector',
  templateUrl: './cbc-lang-selector.component.html',
  styleUrls: ['./cbc-lang-selector.component.css']
})
export class CbcLangSelectorComponent implements OnInit {

  @Input()
  public compactMode = false;
  
  constructor() { }

  ngOnInit(): void {
  }

}
