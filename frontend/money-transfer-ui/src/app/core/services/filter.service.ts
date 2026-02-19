import { Injectable } from "@angular/core";

@Injectable({  providedIn: 'root'})
export class FilterService {

  applyFilters<T>(data: T[], filters: { [key: string]: any }): T[] {
    return data.filter(item => {

      for (const key in filters) {
        const value = filters[key];

        if (value === null || value === '' || value === 'ALL') {
          continue;
        }

        if ((item as any)[key] !== value) {
          return false;
        }
      }

      return true;
    });
  }

}