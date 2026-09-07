export default function GlassCard({ children, className = '', ...props }) {
  return (
    <div className={`glass glass-card ${className}`} {...props}>
      {children}
    </div>
  );
}