import { ComponentFixture, TestBed } from '@angular/core/testing';

import { foroComponent } from './foro.component';

describe('foroComponent', () => {
  let component: foroComponent;
  let fixture: ComponentFixture<foroComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [foroComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(foroComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
