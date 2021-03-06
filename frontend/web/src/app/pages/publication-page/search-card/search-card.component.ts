import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {CardComponent} from '../../../shared/card/card.component';
import {BookSearchService} from '../../../core/service/book-search.service';
import {Publication} from '../../../core/model/publication';
import {Observable} from 'rxjs/Observable';

@Component({
  selector: 'app-search-card',
  templateUrl: './search-card.component.html',
  styleUrls: ['./search-card.component.scss']
})
export class SearchCardComponent extends CardComponent implements OnInit {
  @Input()
  searchValue: string;

  @Output()
  searchComplete: EventEmitter<Publication[]> = new EventEmitter();

  searchButtonDisabled: boolean = false;

  constructor(private bookSearchService: BookSearchService) {
    super();
  }

  ngOnInit() {
  }

  search() {
    if (this.searchValue) {
      this.searchButtonDisabled = true;
      let publicationSearchResult: Observable<Publication[]> = this.bookSearchService.search(this.searchValue);
      publicationSearchResult.subscribe({
        next: (response: Publication[]) => {
          this.searchButtonDisabled = false;
          this.searchComplete.emit(response);
        },
        error: (response: Publication[]) => {
          this.searchButtonDisabled = false;
        }
      });
    }

  }

}
