import { Page } from './page';
import { Rate } from './rate';

export interface RatePage extends Page {
    data: Rate[];
}
