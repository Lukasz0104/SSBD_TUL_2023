import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TruncatePipe } from './pipes/truncate.pipe';
import { ConfirmActionComponent } from './components/confirm-action/confirm-action.component';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { TranslateHttpLoader } from '@ngx-translate/http-loader';
import { HttpClient } from '@angular/common/http';
import { NgbModule } from '@ng-bootstrap/ng-bootstrap';
import { RefreshSessionComponent } from './components/refresh-session/refresh-session.component';

@NgModule({
    declarations: [
        TruncatePipe,
        ConfirmActionComponent,
        RefreshSessionComponent
    ],
    imports: [
        CommonModule,
        NgbModule,
        TranslateModule.forRoot({
            loader: {
                provide: TranslateLoader,
                useFactory: HttpLoaderFactory,
                deps: [HttpClient]
            }
        })
    ],
    exports: [TruncatePipe, ConfirmActionComponent, TranslateModule, NgbModule]
})
export class SharedModule {}

export function HttpLoaderFactory(http: HttpClient): TranslateHttpLoader {
    return new TranslateHttpLoader(http);
}
