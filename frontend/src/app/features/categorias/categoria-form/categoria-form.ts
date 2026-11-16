import { Component, effect, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Categoria, CategoriaInput } from '../../../models/categoria.model';
import { CategoriaService } from '../../../core/services/categoria.service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-categoria-form',
  styleUrl: './categoria-form.css',
  templateUrl: './categoria-form.html',
})
export class CategoriaForm {
  categoria = input<Categoria | null>(null);
  salvou = output<void>();
  cancelou = output<void>();
  erro = '';

  formulario = new FormGroup({
    descricao: new FormControl('', {
      nonNullable: true,
      validators: [
        Validators.required,
        Validators.minLength(3),
        Validators.maxLength(60)
      ]
    }),
    tipo: new FormControl<'RECEITA' | 'DESPESA'>('DESPESA', { nonNullable: true }),
    ativa: new FormControl(true, { nonNullable: true })
  });

  constructor(private categoriaService: CategoriaService) {
    effect(() => {
      const categoria = this.categoria();
      this.erro = '';
      this.formulario.reset(categoria ? {
        descricao: categoria.descricao,
        tipo: categoria.tipo,
        ativa: categoria.ativa
      } : {
        descricao: '',
        tipo: 'DESPESA',
        ativa: true
      });
    });
  }

  salvar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    this.erro = '';
    const dados: CategoriaInput = this.formulario.getRawValue();
    const categoria = this.categoria();

    if (categoria === null) {
      this.categoriaService.cadastrar(dados).subscribe({
        next: () => {
          this.limpar();
          this.salvou.emit();
        },
        error: erro => {
          this.erro = 'Ocorreu um erro ao gravar.';
          console.error(erro);
        }
      });
    } else {
      this.categoriaService.atualizar(categoria.id, dados).subscribe({
        next: () => {
          this.limpar();
          this.salvou.emit();
        },
        error: erro => {
          this.erro = erro.status === 404
            ? 'Categoria não encontrada para atualização.'
            : 'Ocorreu um erro ao atualizar.';
          console.error(erro);
        }
      });
    }
  }

  cancelar(): void {
    this.erro = '';
    this.limpar();
    this.cancelou.emit();
  }

  private limpar(): void {
    this.formulario.reset({ descricao: '', tipo: 'DESPESA', ativa: true });
  }
}
