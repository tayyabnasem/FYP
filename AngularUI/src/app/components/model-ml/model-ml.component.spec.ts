import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModelMLComponent } from './model-ml.component';

describe('ModelMLComponent', () => {
  let component: ModelMLComponent;
  let fixture: ComponentFixture<ModelMLComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModelMLComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModelMLComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
