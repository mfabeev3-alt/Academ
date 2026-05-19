/**
 * @license
 * SPDX-License-Identifier: Apache-2.0
 */

import React, { useState, useEffect } from 'react';
import { motion, AnimatePresence } from 'motion/react';
import { 
  BookOpen, 
  Users, 
  Calendar, 
  Plus, 
  Trash2, 
  Edit2, 
  LogOut,
  AlertTriangle,
  Loader2,
  Clock,
  MapPin,
  CheckCircle2,
  X
} from 'lucide-react';
import api, { handleApiError } from './api';
import { 
  SubjectResponseDto, 
  ProfessorResponseDto, 
  ScheduleResponseDto, 
  EventResponseDto,
  DayOfWeek,
  LocalTime
} from './types';
import ErrorModal from './components/ErrorModal';
import TestingInstructions from './components/TestingInstructions';

type Tab = 'schedule' | 'subjects' | 'professors' | 'events';

export default function App() {
  const [activeTab, setActiveTab] = useState<Tab>('schedule');
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [showDeleteAllConfirm, setShowDeleteAllConfirm] = useState(false);

  // Data states
  const [subjects, setSubjects] = useState<SubjectResponseDto[]>([]);
  const [professors, setProfessors] = useState<ProfessorResponseDto[]>([]);
  const [schedule, setSchedule] = useState<ScheduleResponseDto[]>([]);
  const [events, setEvents] = useState<EventResponseDto[]>([]);

  // Form states
  const [isAdding, setIsAdding] = useState(false);
  const [editingItem, setEditingItem] = useState<any>(null);
  const [formData, setFormData] = useState<any>({});

  const days = [
    { value: DayOfWeek.MONDAY, label: 'Понедельник' },
    { value: DayOfWeek.TUESDAY, label: 'Вторник' },
    { value: DayOfWeek.WEDNESDAY, label: 'Среда' },
    { value: DayOfWeek.THURSDAY, label: 'Четверг' },
    { value: DayOfWeek.FRIDAY, label: 'Пятница' },
    { value: DayOfWeek.SATURDAY, label: 'Суббота' },
    { value: DayOfWeek.SUNDAY, label: 'Воскресенье' },
  ];

  const getDayLabel = (day: string) => {
    return days.find(d => d.value === day)?.label || day;
  };

  const handleOpenAdd = () => {
    setEditingItem(null);
    setFormData(activeTab === 'schedule' ? {
      dayOfWeek: DayOfWeek.MONDAY,
      startTime: '09:00',
      endTime: '10:30',
      activeWeeks: '1,2,3,4'
    } : {});
    setIsAdding(true);
  };

  const handleOpenEdit = (item: any) => {
    setEditingItem(item);
    if (activeTab === 'schedule') {
      const getFormattedTime = (time: any) => {
        if (typeof time === 'object' && time !== null && 'hour' in time) {
          return `${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}`;
        }
        if (typeof time === 'string') {
          const parts = time.split(':');
          return parts.slice(0, 2).join(':');
        }
        return '09:00';
      };

      setFormData({
        ...item,
        startTime: getFormattedTime(item.startTime),
        endTime: getFormattedTime(item.endTime),
        activeWeeks: Array.isArray(item.activeWeeks) ? item.activeWeeks.join(',') : ''
      });
    } else if (activeTab === 'events') {
      setFormData({
        ...item,
        date: item.date ? item.date.split('.')[0] : '' // Handle LocalDateTime string
      });
    } else {
      setFormData(item);
    }
    setIsAdding(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      let endpoint = '';
      if (activeTab === 'subjects') endpoint = '/subject';
      else if (activeTab === 'professors') endpoint = '/professor';
      else if (activeTab === 'events') endpoint = '/event';
      else endpoint = '/schedule';

      const payload = { ...formData };
      if (activeTab === 'schedule') {
        payload.activeWeeks = formData.activeWeeks.split(',').map((w: string) => parseInt(w.trim())).filter((w: number) => !isNaN(w));
        // Append :00 to satisfy HH:mm:ss if needed
        if (payload.startTime && payload.startTime.length === 5) payload.startTime += ':00';
        if (payload.endTime && payload.endTime.length === 5) payload.endTime += ':00';
      }

      if (editingItem) {
        await api.patch(`${endpoint}/${editingItem.id}`, payload);
      } else {
        await api.post(`${endpoint}/add`, payload);
      }
      
      setIsAdding(false);
      fetchInitialData();
    } catch (err) {
      setError(handleApiError(err));
    }
  };

  const handleDelete = async (id: number) => {
    try {
      let endpoint = '';
      if (activeTab === 'subjects') endpoint = '/subject';
      else if (activeTab === 'professors') endpoint = '/professor';
      else if (activeTab === 'events') endpoint = '/event';
      else endpoint = '/schedule';

      await api.delete(`${endpoint}/${id}`);
      fetchInitialData();
    } catch (err) {
      setError(handleApiError(err));
    }
  };

  useEffect(() => {
    handleInitData();
  }, []);

  const handleInitData = async () => {
    // Пробуем достать данные из URL (как в Telegram Mini App)
    const rawData = window.location.hash.slice(1) || window.location.search.slice(1);
    
    if (rawData) {
      const params = new URLSearchParams(rawData);
      const authData: Record<string, string> = {};
      params.forEach((value, key) => {
        authData[key] = value;
      });

      if (authData.hash) {
        try {
          const response = await api.post('/api/auth/login', { data: authData });
          localStorage.setItem('auth_token', response.data.token);
          setIsAuthenticated(true);
          fetchInitialData();
        } catch (err) {
          setError(handleApiError(err));
        }
      }
    } else {
      // Проверяем существующий токен
      const token = localStorage.getItem('auth_token');
      if (token) {
        setIsAuthenticated(true);
        fetchInitialData();
      }
    }
    setIsLoading(false);
  };

  const fetchInitialData = async () => {
    try {
      const [subs, profs, sched, evs] = await Promise.all([
        api.get<SubjectResponseDto[]>('/subject'),
        api.get<ProfessorResponseDto[]>('/professor'),
        api.get<ScheduleResponseDto[]>('/schedule'),
        api.get<EventResponseDto[]>('/event')
      ]);
      setSubjects(subs.data);
      setProfessors(profs.data);
      setSchedule(sched.data);
      setEvents(evs.data);
    } catch (err) {
      setError(handleApiError(err));
    }
  };

  const formatLocalTime = (time: any) => {
    if (!time) return '--:--';
    
    // Если это объект LocalTime из схемы
    if (typeof time === 'object' && 'hour' in time && 'minute' in time) {
      return `${time.hour.toString().padStart(2, '0')}:${time.minute.toString().padStart(2, '0')}`;
    }
    
    // Если бекенд прислал строку "HH:mm:ss" или "HH:mm"
    if (typeof time === 'string') {
      return time.split(':').slice(0, 2).join(':');
    }
    
    return '--:--';
  };

  const handleDeleteAllSchedule = async () => {
    try {
      await api.delete('/schedule/all');
      setSchedule([]);
      setShowDeleteAllConfirm(false);
    } catch (err) {
      setError(handleApiError(err));
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('auth_token');
    setIsAuthenticated(false);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-gray-50">
        <Loader2 className="w-8 h-8 text-blue-600 animate-spin" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen bg-gray-50 p-6 flex flex-col items-center">
        <div className="max-w-md w-full bg-white p-8 rounded-2xl shadow-sm border border-gray-100 mt-12">
          <div className="flex justify-center mb-6">
            <div className="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center">
              <Calendar className="w-8 h-8 text-blue-600" />
            </div>
          </div>
          <h1 className="text-2xl font-bold text-center text-gray-900 mb-2">Управление Расписанием</h1>
          <p className="text-center text-gray-500 mb-8">Войдите через Telegram для управления данными вашей группы.</p>
          
          <TestingInstructions />
          
          <div className="text-center text-xs text-gray-400">
            Ожидание авторизации Telegram...
          </div>
        </div>
        <ErrorModal error={error} onClose={() => setError(null)} />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gray-50 text-gray-900 pb-20">
      {/* Header */}
      <header className="bg-white border-b border-gray-200 sticky top-0 z-30">
        <div className="max-w-4xl mx-auto px-4 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2 font-bold text-xl text-blue-600">
            <Calendar className="w-6 h-6" />
            <span>Староста</span>
          </div>
          <button 
            onClick={handleLogout}
            className="p-2 text-gray-400 hover:text-red-500 transition-colors"
            id="logout-btn"
          >
            <LogOut className="w-5 h-5" />
          </button>
        </div>
      </header>

      <main className="max-w-4xl mx-auto px-4 py-6">
        <AnimatePresence mode="wait">
          {activeTab === 'schedule' && (
            <motion.div
              key="schedule"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold">Расписание</h2>
                <div className="flex gap-2">
                  <button 
                    onClick={() => setShowDeleteAllConfirm(true)}
                    className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-red-600 bg-red-50 rounded-lg hover:bg-red-100 transition-colors"
                    id="delete-all-schedule-btn"
                  >
                    <Trash2 className="w-4 h-4" />
                    Очистить всё
                  </button>
                  <button 
                    onClick={handleOpenAdd}
                    className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
                    id="add-schedule-btn"
                  >
                    <Plus className="w-4 h-4" />
                    Добавить
                  </button>
                </div>
              </div>

              {schedule.length === 0 ? (
                <div className="text-center py-12 bg-white rounded-xl border border-dashed border-gray-300">
                  <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                  <p className="text-gray-500">Записей пока нет</p>
                </div>
              ) : (
                <div className="space-y-4">
                  {schedule.map((item) => (
                    <div key={item.id} className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex justify-between items-start">
                      <div>
                        <div className="font-bold text-lg mb-1">{item.subjectName}</div>
                        <div className="flex flex-wrap gap-x-4 gap-y-2 text-sm text-gray-500 mb-2">
                          <div className="flex items-center gap-1 font-medium text-blue-700 bg-blue-50 px-2 py-0.5 rounded">
                            <Calendar className="w-3.5 h-3.5" />
                            {getDayLabel(item.dayOfWeek)}
                          </div>
                          <div className="flex items-center gap-1">
                            <Clock className="w-4 h-4" />
                            {formatLocalTime(item.startTime)} - {formatLocalTime(item.endTime)}
                          </div>
                          <div className="flex items-center gap-1">
                            <Users className="w-4 h-4" />
                            {item.professorName}
                          </div>
                          <div className="flex items-center gap-1">
                            <MapPin className="w-4 h-4" />
                            Ауд. {item.room}
                          </div>
                        </div>
                        <div className="text-xs text-gray-400 bg-gray-50 inline-block px-2 py-0.5 rounded">
                          Недели: {item.activeWeeks.join(', ')}
                        </div>
                      </div>
                      <div className="flex gap-1">
                        <button onClick={() => handleOpenEdit(item)} className="p-2 text-gray-400 hover:text-blue-500 transition-colors"><Edit2 className="w-4 h-4" /></button>
                        <button onClick={() => handleDelete(item.id)} className="p-2 text-gray-400 hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                      </div>
                    </div>
                  ))}
                </div>
              )}
            </motion.div>
          )}

          {activeTab === 'subjects' && (
            <motion.div
              key="subjects"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold">Предметы</h2>
                <button 
                  onClick={handleOpenAdd}
                  className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
                  id="add-subject-btn"
                >
                  <Plus className="w-4 h-4" />
                  Добавить
                </button>
              </div>
              <div className="grid gap-3">
                {subjects.map(subject => (
                  <div key={subject.id} className="bg-white px-4 py-3 rounded-lg border border-gray-200 flex items-center justify-between">
                    <span className="font-medium">{subject.name}</span>
                    <div className="flex gap-2">
                       <button onClick={() => handleOpenEdit(subject)} className="p-1.5 text-gray-400 hover:text-blue-500 transition-colors"><Edit2 className="w-4 h-4" /></button>
                       <button onClick={() => handleDelete(subject.id)} className="p-1.5 text-gray-400 hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                    </div>
                  </div>
                ))}
              </div>
            </motion.div>
          )}

          {activeTab === 'professors' && (
            <motion.div
              key="professors"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold">Преподаватели</h2>
                <button 
                  onClick={handleOpenAdd}
                  className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
                  id="add-professor-btn"
                >
                  <Plus className="w-4 h-4" />
                  Добавить
                </button>
              </div>
               <div className="grid gap-3">
                {professors.map(prof => (
                  <div key={prof.id} className="bg-white p-4 rounded-lg border border-gray-200">
                    <div className="flex items-center justify-between mb-1">
                      <span className="font-bold text-gray-900">{prof.name}</span>
                      <div className="flex gap-2">
                        <button onClick={() => handleOpenEdit(prof)} className="p-1.5 text-gray-400 hover:text-blue-500 transition-colors"><Edit2 className="w-4 h-4" /></button>
                        <button onClick={() => handleDelete(prof.id)} className="p-1.5 text-gray-400 hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                      </div>
                    </div>
                    {prof.contact && (
                      <div className="text-sm text-gray-500 flex items-center gap-1">
                        <Users className="w-3 h-3" />
                        {prof.contact}
                      </div>
                    )}
                  </div>
                ))}
              </div>
            </motion.div>
          )}

          {activeTab === 'events' && (
            <motion.div
              key="events"
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -10 }}
            >
              <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-bold">Мероприятия</h2>
                <button 
                  onClick={handleOpenAdd}
                  className="flex items-center gap-2 px-3 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm"
                  id="add-event-btn"
                >
                  <Plus className="w-4 h-4" />
                  Создать
                </button>
              </div>
              <div className="space-y-4">
                {events.length === 0 ? (
                  <div className="text-center py-12 bg-white rounded-xl border border-dashed border-gray-300">
                    <Calendar className="w-12 h-12 text-gray-300 mx-auto mb-3" />
                    <p className="text-gray-500">Мероприятий пока нет</p>
                  </div>
                ) : (
                  events.map(event => (
                    <div key={event.id} className="bg-white p-4 rounded-xl shadow-sm border border-gray-100 flex justify-between items-start">
                      <div>
                        <div className="font-bold text-lg mb-1">{event.name}</div>
                        {event.description && <p className="text-sm text-gray-600 mb-3">{event.description}</p>}
                        <div className="flex items-center gap-2 text-sm text-blue-600 font-medium">
                          <Clock className="w-4 h-4" />
                          {new Date(event.date).toLocaleString('ru-RU', { 
                            day: 'numeric', 
                            month: 'long', 
                            hour: '2-digit', 
                            minute: '2-digit' 
                          })}
                        </div>
                      </div>
                      <div className="flex gap-1">
                        <button onClick={() => handleOpenEdit(event)} className="p-2 text-gray-400 hover:text-blue-500 transition-colors"><Edit2 className="w-4 h-4" /></button>
                        <button onClick={() => handleDelete(event.id)} className="p-2 text-gray-400 hover:text-red-500 transition-colors"><Trash2 className="w-4 h-4" /></button>
                      </div>
                    </div>
                  ))
                )}
              </div>
            </motion.div>
          )}
        </AnimatePresence>
      </main>

      {/* Navigation */}
      <nav className="fixed bottom-0 left-0 right-0 bg-white border-t border-gray-200 px-6 py-3 z-30">
        <div className="max-w-md mx-auto flex justify-between items-center">
          <button
            id="nav-schedule"
            onClick={() => setActiveTab('schedule')}
            className={`flex flex-col items-center gap-1 transition-colors ${activeTab === 'schedule' ? 'text-blue-600' : 'text-gray-400'}`}
          >
            <Calendar className="w-6 h-6" />
            <span className="text-[10px] font-medium uppercase tracking-wider">Расписание</span>
          </button>
          <button
            id="nav-subjects"
            onClick={() => setActiveTab('subjects')}
            className={`flex flex-col items-center gap-1 transition-colors ${activeTab === 'subjects' ? 'text-blue-600' : 'text-gray-400'}`}
          >
            <BookOpen className="w-6 h-6" />
            <span className="text-[10px] font-medium uppercase tracking-wider">Предметы</span>
          </button>
          <button
            id="nav-professors"
            onClick={() => setActiveTab('professors')}
            className={`flex flex-col items-center gap-1 transition-colors ${activeTab === 'professors' ? 'text-blue-600' : 'text-gray-400'}`}
          >
            <Users className="w-6 h-6" />
            <span className="text-[10px] font-medium uppercase tracking-wider">Преподаватели</span>
          </button>
          <button
            id="nav-events"
            onClick={() => setActiveTab('events')}
            className={`flex flex-col items-center gap-1 transition-colors ${activeTab === 'events' ? 'text-blue-600' : 'text-gray-400'}`}
          >
            <Calendar className="w-6 h-6 text-indigo-500" />
            <span className="text-[10px] font-medium uppercase tracking-wider">Мероприятия</span>
          </button>
        </div>
      </nav>

      {/* Delete All Confirmation Modal */}
      <AnimatePresence>
        {showDeleteAllConfirm && (
          <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/50 backdrop-blur-sm">
            <motion.div
              initial={{ scale: 0.95, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.95, opacity: 0 }}
              className="bg-white rounded-2xl p-6 max-w-sm w-full shadow-2xl"
            >
              <div className="w-12 h-12 bg-red-100 rounded-full flex items-center justify-center mb-4 mx-auto text-red-600">
                <AlertTriangle className="w-6 h-6" />
              </div>
              <h3 className="text-lg font-bold text-center mb-2">Удалить всё расписание?</h3>
              <p className="text-center text-gray-500 mb-6 leading-relaxed">
                Это действие <span className="text-red-600 font-semibold underline">удалит все записи</span> занятий вашей группы. Вы не сможете это отменить.
              </p>
              <div className="flex gap-3">
                <button
                  id="cancel-delete-all"
                  onClick={() => setShowDeleteAllConfirm(false)}
                  className="flex-1 py-3 bg-gray-100 text-gray-700 font-semibold rounded-xl hover:bg-gray-200 transition-colors"
                >
                  Отмена
                </button>
                <button
                  id="confirm-delete-all"
                  onClick={handleDeleteAllSchedule}
                  className="flex-1 py-3 bg-red-600 text-white font-semibold rounded-xl hover:bg-red-700 transition-colors shadow-lg"
                >
                  Да, удалить
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>

      <ErrorModal error={error} onClose={() => setError(null)} />

      {/* Add/Edit Modal */}
      <AnimatePresence>
        {isAdding && (
          <div className="fixed inset-0 z-50 flex items-end sm:items-center justify-center p-0 sm:p-4 bg-black/50 backdrop-blur-sm">
            <motion.div
              initial={{ y: "100%" }}
              animate={{ y: 0 }}
              exit={{ y: "100%" }}
              className="bg-white w-full max-w-lg rounded-t-2xl sm:rounded-2xl shadow-2xl overflow-hidden"
            >
              <form onSubmit={handleSubmit}>
                <div className="p-4 border-b border-gray-100 flex items-center justify-between bg-gray-50">
                  <h3 className="font-bold text-lg">
                    {editingItem ? 'Редактировать' : 'Добавить'} {activeTab === 'subjects' ? 'предмет' : activeTab === 'professors' ? 'преподавателя' : activeTab === 'events' ? 'мероприятие' : 'занятие'}
                  </h3>
                  <button type="button" onClick={() => setIsAdding(false)} className="text-gray-400 hover:text-gray-600">
                    <X className="w-6 h-6" />
                  </button>
                </div>
                
                <div className="p-6 space-y-4 max-h-[70vh] overflow-y-auto">
                  {activeTab === 'subjects' && (
                    <div>
                      <label className="block text-sm font-medium text-gray-700 mb-1">Название предмета</label>
                      <input 
                        required
                        className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                        value={formData.name || ''}
                        onChange={e => setFormData({...formData, name: e.target.value})}
                      />
                    </div>
                  )}

                  {activeTab === 'events' && (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Название мероприятия</label>
                        <input 
                          required
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.name || ''}
                          onChange={e => setFormData({...formData, name: e.target.value})}
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Описание (необязательно)</label>
                        <textarea 
                          rows={3}
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.description || ''}
                          onChange={e => setFormData({...formData, description: e.target.value})}
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Дата и время</label>
                        <input 
                          type="datetime-local"
                          required
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.date || ''}
                          onChange={e => setFormData({...formData, date: e.target.value})}
                        />
                      </div>
                    </>
                  )}

                  {activeTab === 'professors' && (
                    <>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">ФИО Преподавателя</label>
                        <input 
                          required
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.name || ''}
                          onChange={e => setFormData({...formData, name: e.target.value})}
                        />
                      </div>
                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Контакт (необязательно)</label>
                        <input 
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.contact || ''}
                          onChange={e => setFormData({...formData, contact: e.target.value})}
                        />
                      </div>
                    </>
                  )}

                  {activeTab === 'schedule' && (
                    <>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Предмет</label>
                          <select 
                            required
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                            value={formData.subjectId || ''}
                            onChange={e => setFormData({...formData, subjectId: parseInt(e.target.value)})}
                          >
                            <option value="">Выберите предмет</option>
                            {subjects.map(s => <option key={s.id} value={s.id}>{s.name}</option>)}
                          </select>
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Преподаватель</label>
                          <select 
                            required
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                            value={formData.professorId || ''}
                            onChange={e => setFormData({...formData, professorId: parseInt(e.target.value)})}
                          >
                            <option value="">Выберите препода</option>
                            {professors.map(p => <option key={p.id} value={p.id}>{p.name}</option>)}
                          </select>
                        </div>
                      </div>

                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">День недели</label>
                          <select 
                            required
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none bg-white"
                            value={formData.dayOfWeek || ''}
                            onChange={e => setFormData({...formData, dayOfWeek: e.target.value as DayOfWeek})}
                          >
                            {days.map(d => <option key={d.value} value={d.value}>{d.label}</option>)}
                          </select>
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Аудитория</label>
                          <input 
                            required
                            placeholder="Напр: 402-б"
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            value={formData.room || ''}
                            onChange={e => setFormData({...formData, room: e.target.value})}
                          />
                        </div>
                      </div>

                      <div className="grid grid-cols-2 gap-4">
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Начало</label>
                          <input 
                            type="time"
                            required
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            value={formData.startTime || ''}
                            onChange={e => setFormData({...formData, startTime: e.target.value})}
                          />
                        </div>
                        <div>
                          <label className="block text-sm font-medium text-gray-700 mb-1">Конец</label>
                          <input 
                            type="time"
                            required
                            className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                            value={formData.endTime || ''}
                            onChange={e => setFormData({...formData, endTime: e.target.value})}
                          />
                        </div>
                      </div>

                      <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Учебные недели (через запятую)</label>
                        <input 
                          required
                          placeholder="Напр: 1,2,3,4"
                          className="w-full px-4 py-2 border border-gray-200 rounded-lg focus:ring-2 focus:ring-blue-500 outline-none"
                          value={formData.activeWeeks || ''}
                          onChange={e => setFormData({...formData, activeWeeks: e.target.value})}
                        />
                      </div>
                    </>
                  )}
                </div>

                <div className="p-6 bg-gray-50 flex gap-3">
                  <button 
                    type="button" 
                    onClick={() => setIsAdding(false)}
                    className="flex-1 py-3 bg-white border border-gray-200 text-gray-700 font-bold rounded-xl hover:bg-gray-100 transition-all"
                  >
                    Отмена
                  </button>
                  <button 
                    type="submit"
                    className="flex-1 py-3 bg-blue-600 text-white font-bold rounded-xl hover:bg-blue-700 shadow-lg shadow-blue-200 transition-all"
                  >
                    {editingItem ? 'Сохранить' : 'Создать'}
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
}
