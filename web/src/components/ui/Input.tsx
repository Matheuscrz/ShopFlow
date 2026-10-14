import React, { forwardRef } from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  error?: string;
}

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, id, ...props }, ref) => {
    const inputId = id || props.name;

    return (
      <div className="flex flex-col gap-1 w-full text-left">
        <label
          htmlFor={inputId}
          className="text-sm font-medium text-text-muted"
        >
          {label}
        </label>
        <input
          id={inputId}
          ref={ref}
          className={`w-full px-3.5 py-2 rounded-lg bg-surface border text-text-main placeholder-text-muted focus:outline-none focus:ring-2 transition-colors ${
            error
              ? "border-danger focus:ring-danger/20"
              : "border-border focus:ring-primary/20 focus:border-primary"
          }`}
          {...props}
        />
        {error && (
          <span className="text-xs text-danger font-medium">{error}</span>
        )}
      </div>
    );
  },
);

Input.displayName = "Input";
