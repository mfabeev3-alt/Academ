import { useState } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { AlertCircle, X } from 'lucide-react';

interface ErrorModalProps {
  error: string | null;
  onClose: () => void;
}

export default function ErrorModal({ error, onClose }: ErrorModalProps) {
  return (
    <AnimatePresence>
      {error && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
          <motion.div
            initial={{ opacity: 0, scale: 0.95 }}
            animate={{ opacity: 1, scale: 1 }}
            exit={{ opacity: 0, scale: 0.95 }}
            className="bg-white rounded-lg shadow-xl max-w-md w-full overflow-hidden"
          >
            <div className="p-4 bg-red-50 flex items-center justify-between border-b border-red-100">
              <div className="flex items-center gap-2 text-red-700 font-semibold">
                <AlertCircle className="w-5 h-5" />
                <span>Ошибка</span>
              </div>
              <button 
                onClick={onClose}
                className="text-red-400 hover:text-red-600 transition-colors"
                id="close-error-modal"
              >
                <X className="w-5 h-5" />
              </button>
            </div>
            <div className="p-6">
              <p className="text-gray-700 leading-relaxed">
                {error}
              </p>
              <div className="mt-6 flex justify-end">
                <button
                  id="error-modal-ok-btn"
                  onClick={onClose}
                  className="px-4 py-2 bg-red-600 text-white rounded-md hover:bg-red-700 transition-colors font-medium shadow-sm"
                >
                  Понятно
                </button>
              </div>
            </div>
          </motion.div>
        </div>
      )}
    </AnimatePresence>
  );
}
