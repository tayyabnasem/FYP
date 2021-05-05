import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModelDLComponent } from './model-dl.component';

describe('ModelDLComponent', () => {
  let component: ModelDLComponent;
  let fixture: ComponentFixture<ModelDLComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModelDLComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModelDLComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
