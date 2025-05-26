const { app, BrowserWindow } = require('electron');
const path = require('path');
const isDev = !app.isPackaged;

function createWindow() {
  // Create the browser window.
  const mainWindow = new BrowserWindow({
    width: 1200,
    height: 800,
    webPreferences: {
      nodeIntegration: true,
      contextIsolation: false,
      webSecurity: false // Solo para desarrollo
    }
  });

  const prodIndex = `file://${path.join(__dirname, 'index.html')}`;
  console.log('Cargando:', isDev ? 'http://localhost:5173' : prodIndex);

  // Load the index.html from a url in dev mode or the local file in production
  mainWindow.loadURL(prodIndex);

  // Open the DevTools in development mode.
  if (isDev) {
    mainWindow.webContents.openDevTools();
  }

  // Handle window state
  mainWindow.on('closed', () => {
    mainWindow = null;
  });
}

// This method will be called when Electron has finished initialization
app.whenReady().then(createWindow);

// Quit when all windows are closed.
app.on('window-all-closed', () => {
  if (process.platform !== 'darwin') {
    app.quit();
  }
});

app.on('activate', () => {
  if (BrowserWindow.getAllWindows().length === 0) {
    createWindow();
  }
});

// Handle any uncaught exceptions
process.on('uncaughtException', (error) => {
  console.error('Uncaught Exception:', error);
}); 