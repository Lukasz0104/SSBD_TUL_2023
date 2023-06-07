import { Page } from '../../shared/model/page';
import { Reading } from './reading';

export interface ReadingPage extends Page {
    data: Reading[];
}
