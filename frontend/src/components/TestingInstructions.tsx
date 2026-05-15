import { useState, useMemo } from 'react';
import { HelpCircle, Terminal, Copy, Check } from 'lucide-react';

export default function TestingInstructions() {
  const [copied, setCopied] = useState(false);
  
  const testUrl = useMemo(() => {
    const baseUrl = window.location.origin;
    const testParams = "query_id=AAEM9Ok8AAAAAAz06Ty3PoPq&user=%7B%22id%22%3A1021965324%2C%22first_name%22%3A%22Eli%22%2C%22last_name%22%3A%22%22%2C%22username%22%3A%22eli_byn%22%2C%22language_code%22%3A%22ru%22%2C%22allows_write_to_pm%22%3Atrue%2C%22photo_url%22%3A%22https%3A%5C%2F%5C%2Ft.me%5C%2Fi%5C%2Fuserpic%5C%2F320%5C%2FBiwuRigdypyvGfXEpt-yfYcoBrZtpaYOTgZnUt07W9w.svg%22%7D&auth_date=1778758266&signature=eL-3QWwAOFHbdFh8ipYGmDIINalbkM78Y7H9I62F4FQqdWFQ9LGxSAXUIG0cy7kuKXZBIaB519l73y3l8AYXCA&hash=1730098132a3e48fbfc624f35ce9d6deec2781ac4cf9d78b7a31f54371b0b9de";
    return `${baseUrl}/#${testParams}`;
  }, []);

  const copyToClipboard = () => {
    navigator.clipboard.writeText(testUrl);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div className="p-4 bg-blue-50 border border-blue-100 rounded-lg mb-6">
      <div className="flex items-center gap-2 text-blue-800 font-semibold mb-2">
        <HelpCircle className="w-5 h-5" />
        <h3>Инструкция для локального тестирования</h3>
      </div>
      <div className="text-sm text-blue-700 space-y-2">
        <p>Для симуляции входа из Telegram, добавьте параметры InitData в URL. Скопируйте ссылку ниже и откройте её в новой вкладке:</p>
        <div className="relative group">
          <code className="block p-3 bg-white border border-blue-200 rounded text-xs break-all pr-12">
            {testUrl}
          </code>
          <button
            id="copy-test-url"
            onClick={copyToClipboard}
            className="absolute right-2 top-2 p-1.5 bg-blue-100 hover:bg-blue-200 rounded text-blue-700 transition-colors"
            title="Копировать"
          >
            {copied ? <Check className="w-4 h-4" /> : <Copy className="w-4 h-4" />}
          </button>
        </div>
        <p className="text-xs italic opacity-80">Примечание: Убедитесь, что ваш бэкенд запущен на порту 8086 или настроен в API_BASE_URL.</p>
      </div>
    </div>
  );
}
