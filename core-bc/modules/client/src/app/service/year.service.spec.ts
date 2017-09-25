import { TestBed, inject } from '@angular/core/testing';

import { YearService } from './year.service';

describe('YearService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [YearService]
    });
  });

  it('should be created', inject([YearService], (service: YearService) => {
    expect(service).toBeTruthy();
  }));
});
