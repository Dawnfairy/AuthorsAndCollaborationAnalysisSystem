# Akademik İşbirliği Analizi - Java Uygulaması

Bu proje, akademik veri setleri kullanılarak yazarlar arasındaki işbirliği ilişkilerini modelleyen, analiz eden ve grafiksel olarak görselleştiren kapsamlı bir Java uygulamasıdır. Projede, yazarlar düğümler (nodes) olarak, ortak makaleler ise kenarlar (edges) olarak modellenmiştir. Kenarlar, yazarların ortak makale sayısına göre ağırlıklandırılmıştır. Projenin amacı, veri yapıları ve algoritmalar konusundaki teorik bilgilerin pratikte nasıl uygulanabileceğini göstermek ve akademik işbirliklerini daha iyi anlamaktır.

## İçindekiler

- [Özellikler](#özellikler)
- [Kullanılan Teknolojiler ve Kütüphaneler](#kullanılan-teknolojiler-ve-kütüphaneler)
- [Veri Yapıları ve Algoritmalar](#veri-yapıları-ve-algoritmalar)
- [Kurulum ve Çalıştırma](#kurulum-ve-çalıştırma)
- [Kullanım](#kullanım)
- [Deneysel Sonuçlar](#deneysel-sonuçlar)

## Özellikler

- **Grafiksel Modelleme:** Yazarlar arasındaki işbirliği ilişkileri grafik üzerinde düğümler ve kenarlar şeklinde temsil edilir.
- **Dinamik Görselleştirme:** Düğümlerin boyutları ve renkleri, yazarların yazdığı makale sayısına göre dinamik olarak ayarlanır; böylece daha fazla makale yazan yazarlar ön plana çıkarılır.
- **Kullanıcı Dostu Arayüz:** Swing kütüphanesi kullanılarak geliştirilen arayüz sayesinde kullanıcılar, analiz sonuçlarını kolaylıkla görüntüleyebilir, kaydırma (zoom in/zoom out) özellikleriyle grafiği inceleyebilir.
- **Özel Veri Yapıları:** CustomMap, CustomSet, CustomLinkedList ve CustomPriorityQueue gibi yapıların yanı sıra, ikili arama ağacı (BST) gibi veri yapıları kullanılarak veriler etkin bir şekilde yönetilir.
- **Algoritmalar:** 
  - **Dijkstra'nın Algoritması:** İki yazar arasındaki en kısa yolun bulunması.
  - **Secmeli Sıralama (Selection Sort):** A yazarının işbirliği yaptığı yazarların, yazdıkları makale sayısına göre sıralanması.
  - **Derinlemesine Öncelikli Arama (DFS):** A yazarından başlayarak, en uzun işbirliği yolunun bulunması.
  - **Genişletilmiş Genişlik Öncelikli Arama (BFS):** A yazarının diğer işbirliği yaptığı yazarlarla olan en kısa yolların tespiti.
  - **BST İşlemleri:** Kuyruktaki yazarların BST’ye eklenmesi, arama, ekleme ve silme işlemlerinin gerçekleştirilmesi.

## Kullanılan Teknolojiler ve Kütüphaneler

- **Java SE:** Uygulamanın temel programlama dili.
- **Swing:** Grafiksel kullanıcı arayüzü geliştirmek için.
- **Apache POI:** Excel dosyalarından veri okuma ve işleme.
- **JGraphX:** Grafiğin görselleştirilmesi için.
- **Özel Veri Yapıları:** `CustomMap`, `CustomSet`, `CustomLinkedList`, `CustomPriorityQueue`, ve `AuthorBST`.

## Veri Yapıları ve Algoritmalar

### Özel Veri Yapıları

- **CustomMap:** Yazarlar ve kenarlar arasındaki ilişkileri depolamak için kullanılır. Adjacency list yapısını destekler.
- **CustomSet:** Tekrarlayan öğeleri önleyerek benzersiz yazarları saklar.
- **CustomLinkedList:** Yazarların mesafeleri ve önceki yazar bilgilerini takip etmek için kullanılır.
- **CustomPriorityQueue:** Algoritmalarda en kısa mesafeye sahip yazarları hızlıca seçmek için.
- **AuthorBST:** Kuyruktaki yazarların makale sayılarına göre düzenlenmesi amacıyla ikili arama ağacı.

### Algoritmalar

- **Dijkstra’nın En Kısa Yol Algoritması:**
  - Başlangıç düğümünden tüm düğümlere en kısa yollar hesaplanır.
  - Öncelik kuyruğu kullanılarak, en kısa mesafeye sahip yazarlar işlenir.
  - Hedef düğüme ulaşıldığında yol geri izlenerek en kısa yol belirlenir.

- **Secmeli Sıralama (Selection Sort):**
  - A yazarının işbirliği yaptığı yazarlar, makale sayılarına göre azalan sırada sıralanır.
  - Sıralanan yazarlar, öncelik kuyruğuna eklenir ve arayüzde görselleştirilir.

- **Derinlemesine Öncelikli Arama (DFS):**
  - A yazarından başlayarak en uzun işbirliği yolunu bulur.
  - Bulunan yol, detay panelinde ve grafik üzerinde vurgulanır.

- **Genişletilmiş Genişlik Öncelikli Arama (BFS):**
  - A yazarından başlayan tüm en kısa yollar hesaplanır ve görsel olarak sunulur.

- **İkili Arama Ağacı (BST):**
  - Kuyruktaki yazarların makale sayılarına göre BST’ye eklenmesi, arama, ekleme ve silme işlemleri gerçekleştirilir.
  - BST’nin hiyerarşik yapısı kullanıcıya grafiksel olarak sunulur.

## Kurulum ve Çalıştırma

### Gereksinimler

- Java SE 8 veya üzeri
- Maven (opsiyonel; proje bağımlılıklarını yönetmek için)
- Git

### Adımlar

1. **Repository’yi Klonlayın:**

   ```bash
   git clone https://github.com/Dawnfairy/AuthorsAndCollaborationAnalysisSystem.git
   cd AuthorsAndCollaborationAnalysisSystem

2. **Gerekli Kütüphaneleri Ekleyin:**
   - Projede kullanılan Apache POI ve JGraphX kütüphanelerinin projeye dahil edildiğinden emin olun.
   - Eğer Maven kullanıyorsanız, `pom.xml` dosyasındaki bağımlılıkların indirildiğini kontrol edin.

3. **Projeyi Açın ve Derleyin:**
   - Tercih ettiğiniz Java IDE’sinde projeyi açın.
   - Projeyi derleyip çalıştırın. Ana sınıfı (`Main`) çalıştırarak uygulamayı başlatabilirsiniz.

## Kullanım

1. **Veri Girişi:** Excel dosyasından makale başlıkları, yazar isimleri ve DOI bilgileri okunarak, veriler işlenir.
2. **Grafik Modelinin Oluşturulması:** Yazarlar düğüm olarak, ortak makaleler kenar olarak modele eklenir.
3. **Analiz Seçenekleri:**
   - İki yazar arasındaki en kısa yolun bulunması (Dijkstra).
   - A yazarının işbirliği yaptığı yazarların sıralanması (Selection Sort).
   - Kuyruktaki yazarların BST’ye eklenmesi ve görselleştirilmesi.
   - A yazarından başlayarak en uzun işbirliği yolunun bulunması (DFS).
   - A yazarının toplam işbirliği yaptığı yazar sayısının hesaplanması.
4. **Grafiksel Görselleştirme:** Swing arayüzü üzerinden grafikte dinamik güncellemeler, zoom in/zoom out özellikleri ve detaylı analiz sonuçları görüntülenir.

## Deneysel Sonuçlar

- **En Kısa Yol:** A ile B yazarları arasında A → C → D → B şeklinde 3 aracı yazar ile en kısa yol bulunmuştur.
- **İşbirliği Sıralaması:** A yazarının işbirliği yaptığı yazarlar; en çok işbirliği yapılan sıralamaya göre (örneğin, Yazar C: 15, Yazar D: 12, Yazar E: 8 makale) listelenmiştir.
- **BST Görselleştirmesi:** Kuyruktaki yazarlar BST’ye eklenmiş ve arama, ekleme, silme işlemleri gerçekleştirilmiştir.
- **En Uzun Yol:** A yazarından başlayarak A → F → G → H → B şeklinde 4 yazarın geçtiği en uzun yol bulunmuştur.
- **Toplam İşbirliği Sayısı:** A yazarının toplamda 10 işbirliği yaptığı belirlenmiştir.
- **En Çok İşbirliği Yapan Yazar:** Tüm analizler sonucunda en çok işbirliği yapan yazar Yazar C olarak saptanmıştır.
