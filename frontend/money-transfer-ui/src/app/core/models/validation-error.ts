export interface ValidationError {
  message: string;
  errors: Record<string, string>;
  errorCode?: string;
  path?: string;
  timestamp?: string;
}