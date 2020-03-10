import { Component, OnInit, Input } from '@angular/core';
import { Product } from 'src/app/common/product.model';

@Component({
  selector: 'app-product-tile',
  templateUrl: './product-tile.component.html',
  styleUrls: ['./product-tile.component.css']
})
export class ProductTileComponent implements OnInit {

  @Input()
  product:Product = new Product();

  constructor() { }

  ngOnInit() {
  }

}
