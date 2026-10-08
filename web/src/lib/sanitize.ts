import DOMPurify from "dompurify";

export const sanitizeText = (dirty: string): string => {
  return DOMPurify.sanitize(dirty, { ALLOWED_TAGS: [], ALLOWED_ATTR: [] });
};
